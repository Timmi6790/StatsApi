package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder;

import com.google.common.collect.Lists;
import de.timmi6790.mpstats.api.versions.v1.common.filter.FilterService;
import de.timmi6790.mpstats.api.versions.v1.common.filter.models.Reason;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_cache.LeaderboardCacheService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_request.LeaderboardRequestService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.LeaderboardSaveService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.tasks.LeaderboardUpdateTask;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardPositionSave;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardSave;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.utilities.LeaderboardConverter;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class LeaderboardSaveCombinerService<P extends Player, S extends PlayerService<P>> {
    private final LeaderboardCacheService<P> leaderboardCacheService;
    private final LeaderboardSaveService<P> leaderboardSaveService;
    private final FilterService<P, S> filterService;

    private final LeaderboardUpdateTask<P> updateTask;

    public LeaderboardSaveCombinerService(final LeaderboardService leaderboardService,
                                          final LeaderboardRequestService<P> leaderboardRequestService,
                                          final LeaderboardCacheService<P> leaderboardCacheService,
                                          final LeaderboardSaveService<P> leaderboardSaveService,
                                          final FilterService<P, S> filterService) {
        this.leaderboardCacheService = leaderboardCacheService;
        this.leaderboardSaveService = leaderboardSaveService;
        this.filterService = filterService;

        this.updateTask = new LeaderboardUpdateTask<>(
                leaderboardService,
                leaderboardRequestService,
                leaderboardCacheService,
                leaderboardSaveService
        );
    }

    @Scheduled(fixedDelay = 900_000)
    private void updateTask() {
        this.updateTask.updateLeaderboards();
    }

    protected Optional<LeaderboardSave<P>> getLeaderboardEntries(final Leaderboard leaderboard,
                                                                 final ZonedDateTime saveTime) {
        // Always run it against the save service if it is deprecated or the requested time is not from today
        // We also fall back to it if nothing is found in the cache
        if (!leaderboard.isDeprecated() && saveTime.toLocalDate().isEqual(LocalDate.now())) {
            final Optional<LeaderboardSave<P>> cacheOpt = this.leaderboardCacheService.retrieveLeaderboardEntryPosition(leaderboard);
            if (cacheOpt.isPresent()) {
                return cacheOpt;
            }
        }
        return this.leaderboardSaveService.retrieveLeaderboardSave(leaderboard, saveTime);
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
}
