package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder;

import com.google.common.collect.Lists;
import de.timmi6790.mpstats.api.versions.v1.common.filter.FilterService;
import de.timmi6790.mpstats.api.versions.v1.common.filter.models.Reason;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.LeaderboardSaveService;
import de.timmi6790.mpstats.api.versions.v1.common.models.*;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.utilities.LeaderboardConverter;
import de.timmi6790.mpstats.api.versions.v1.common.utilities.PositionCalculation;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Log4j2
public class LeaderboardSaveCombinerService<P extends Player, S extends PlayerService<P>> {
    private final LeaderboardSaveService<P> leaderboardSaveService;
    private final FilterService<P, S> filterService;

    public LeaderboardSaveCombinerService(final LeaderboardService leaderboardService,
                                          final LeaderboardSaveService<P> leaderboardSaveService,
                                          final FilterService<P, S> filterService) {
        this.leaderboardSaveService = leaderboardSaveService;
        this.filterService = filterService;
    }

    protected Optional<LeaderboardSave<P>> getLeaderboardEntries(final Leaderboard leaderboard,
                                                                 final ZonedDateTime saveTime) {
        return this.leaderboardSaveService.retrieveLeaderboardSave(leaderboard, saveTime);
    }

    @SneakyThrows
    protected Map<Leaderboard, LeaderboardSave<P>> getLeaderboardEntries(final Collection<Leaderboard> leaderboards,
                                                                         final ZonedDateTime saveTime) {
        final List<Leaderboard> repositoryEntries = new ArrayList<>(leaderboards);

        // Get resources from both ways
        final CompletableFuture<Map<Leaderboard, LeaderboardSave<P>>> repositoryFuture = CompletableFuture.supplyAsync(
                () -> this.leaderboardSaveService.retrieveLeaderboardSaves(repositoryEntries, saveTime)
        );
        final CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(repositoryFuture);

        try {
            combinedFuture.get(40, TimeUnit.SECONDS);
        } catch (final ExecutionException | TimeoutException e) {
            log.catching(e);
            return new HashMap<>();
        }

        // Combine values
        return new HashMap<>(repositoryFuture.get());
    }

    protected Optional<LeaderboardPlayerPositionSave<P>> getPlayerPositionSave(final LeaderboardSave<P> save,
                                                                               final Leaderboard leaderboard,
                                                                               final Player player,
                                                                               final Set<Reason> filterReasons) {
        final PositionCalculation positionCalculation = new PositionCalculation();
        for (final LeaderboardEntry<P> entry : save.getEntries()) {
            if (!this.filterService.isFiltered(entry.getPlayer(), leaderboard, save.getSaveTime(), filterReasons)) {
                positionCalculation.addScore(entry.getScore());
                if (entry.getPlayer().getRepositoryId() == player.getRepositoryId()) {
                    return Optional.of(
                            new LeaderboardPlayerPositionSave<>(
                                    leaderboard,
                                    save.getSaveTime(),
                                    new LeaderboardPositionEntry<>(
                                            entry,
                                            positionCalculation.getPosition()
                                    )
                            )
                    );
                }
            }
        }
        return Optional.empty();
    }

    public Optional<LeaderboardPositionSave<P>> getLeaderboardSave(final Leaderboard leaderboard,
                                                                   final ZonedDateTime saveTime) {
        final Optional<LeaderboardSave<P>> saveOpt = this.getLeaderboardEntries(leaderboard, saveTime);
        if (saveOpt.isEmpty()) {
            return Optional.empty();
        }

        final LeaderboardSave<P> save = saveOpt.get();
        return Optional.of(
                new LeaderboardPositionSave<>(
                        leaderboard,
                        save.getSaveTime(),
                        LeaderboardConverter.convertEntries(save.getEntries())
                )
        );
    }

    public Optional<LeaderboardPositionSave<P>> getLeaderboardSave(final Leaderboard leaderboard,
                                                                   final ZonedDateTime saveTime,
                                                                   final Set<Reason> filterReasons) {
        final Optional<LeaderboardSave<P>> saveOpt = this.getLeaderboardEntries(leaderboard, saveTime);
        if (saveOpt.isEmpty()) {
            return Optional.empty();
        }

        final LeaderboardSave<P> save = saveOpt.get();
        final List<LeaderboardEntry<P>> filteredEntries = Lists.newArrayListWithExpectedSize(save.getEntries().size());
        for (final LeaderboardEntry<P> entry : save.getEntries()) {
            if (!this.filterService.isFiltered(entry.getPlayer(), leaderboard, save.getSaveTime(), filterReasons)) {
                filteredEntries.add(entry);
            }
        }

        return Optional.of(
                new LeaderboardPositionSave<>(
                        leaderboard,
                        save.getSaveTime(),
                        LeaderboardConverter.convertEntries(filteredEntries)
                )
        );
    }

    public Map<Leaderboard, LeaderboardPlayerPositionSave<P>> getLeaderboardPlayerSaves(final Collection<Leaderboard> leaderboards,
                                                                                        final ZonedDateTime saveTime,
                                                                                        final Player player,
                                                                                        final Set<Reason> filterReasons) {
        final Map<Leaderboard, LeaderboardSave<P>> saves = this.getLeaderboardEntries(leaderboards, saveTime);
        if (saves.isEmpty()) {
            return Collections.emptyMap();
        }

        final Map<Leaderboard, LeaderboardPlayerPositionSave<P>> formattedSaves = new HashMap<>();
        for (final Map.Entry<Leaderboard, LeaderboardSave<P>> entry : saves.entrySet()) {
            this.getPlayerPositionSave(entry.getValue(), entry.getKey(), player, filterReasons)
                    .ifPresent(formatted -> formattedSaves.put(entry.getKey(), formatted));
        }
        return formattedSaves;
    }

    public Optional<LeaderboardPlayerPositionSave<P>> getLeaderboardPlayerSave(final Leaderboard leaderboard,
                                                                               final ZonedDateTime saveTime,
                                                                               final Player player,
                                                                               final Set<Reason> filterReasons) {
        return this.getLeaderboardEntries(leaderboard, saveTime)
                .flatMap(save -> this.getPlayerPositionSave(save, leaderboard, player, filterReasons));
    }
}
