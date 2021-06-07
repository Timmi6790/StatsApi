package de.timmi6790.mpstats.api.versions.v1.common.player_stats;

import com.google.common.collect.Sets;
import de.timmi6790.mpstats.api.versions.v1.common.filter.models.Reason;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.LeaderboardSaveCombinerService;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardPositionEntry;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.generator.StatGenerator;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.generator.StatGeneratorData;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.generator.generators.*;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.generator.generators.cakewars.CakeBitesPerGameGenerator;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.models.GeneratedPlayerEntry;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.models.PlayerEntry;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.models.PlayerStats;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerStatsService<P extends Player, S extends PlayerService<P>> {
    private final LeaderboardSaveCombinerService<P, S> leaderboardSaveCombinerService;

    private final StatGenerator[] statGenerators;

    public PlayerStatsService(final LeaderboardSaveCombinerService<P, S> leaderboardSaveCombinerService) {
        this.leaderboardSaveCombinerService = leaderboardSaveCombinerService;

        this.statGenerators = new StatGenerator[]{
                new CakeBitesPerGameGenerator(),
                new ExpPerGameGenerator(),
                new GemsPerGameGenerator(),
                new KillAssistsRatioGenerator(),
                new KillsPerDeathRatioGenerator(),
                new LevelGenerator(),
                new WinLoseRatioGenerator()
        };
    }

    protected PlayerEntry getEmptyPlayerEntry(final Leaderboard leaderboard) {
        return new PlayerEntry(
                leaderboard,
                LocalDate.EPOCH.atStartOfDay(ZoneId.systemDefault()),
                -1,
                -1
        );
    }

    protected Set<GeneratedPlayerEntry> generateStats(final Set<PlayerEntry> playerStats) {
        final StatGeneratorData generatorData = new StatGeneratorData(playerStats);
        final Set<GeneratedPlayerEntry> generatedStats = new HashSet<>();
        for (final StatGenerator statGenerator : this.statGenerators) {
            generatedStats.addAll(statGenerator.generateStats(generatorData));
        }

        return generatedStats;
    }

    protected Map<Leaderboard, PlayerEntry> getPlayerEntries(final List<Leaderboard> leaderboards,
                                                             final P player,
                                                             final ZonedDateTime time,
                                                             final Set<Reason> filterReasons) {
        return leaderboards.parallelStream()
                .map(leaderboard -> this.leaderboardSaveCombinerService.getLeaderboardSave(leaderboard, time, filterReasons))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(save -> {
                    for (final LeaderboardPositionEntry<P> entry : save.getEntries()) {
                        if (entry.getPlayer().getRepositoryId() == player.getRepositoryId()) {
                            return new PlayerEntry(
                                    save.getLeaderboard(),
                                    save.getSaveTime(),
                                    entry.getScore(),
                                    entry.getPosition()
                            );
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(PlayerEntry::getLeaderboard, entry -> entry));
    }

    public Optional<PlayerStats<P>> getPlayerStats(final List<Leaderboard> leaderboards,
                                                   final P player,
                                                   final ZonedDateTime time,
                                                   final Set<Reason> filterReasons,
                                                   final boolean includeEmptyEntries) {
        final Map<Leaderboard, PlayerEntry> statMap = this.getPlayerEntries(leaderboards, player, time, filterReasons);
        if (statMap.isEmpty()) {
            return Optional.empty();
        }

        final Set<PlayerEntry> stats;
        if (includeEmptyEntries) {
            stats = Sets.newHashSetWithExpectedSize(leaderboards.size());
            for (final Leaderboard leaderboard : leaderboards) {
                final PlayerEntry foundEntry = statMap.get(leaderboard);
                if (foundEntry == null) {
                    stats.add(this.getEmptyPlayerEntry(leaderboard));
                } else {
                    stats.add(foundEntry);
                }
            }
        } else {
            stats = new HashSet<>(statMap.values());
        }

        return Optional.of(
                new PlayerStats<>(
                        player,
                        this.generateStats(stats),
                        stats
                )
        );
    }
}
