package de.timmi6790.mpstats.api.versions.v1.common.player_stats;

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

    protected Set<GeneratedPlayerEntry> generateStats(final Set<PlayerEntry> playerStats) {
        final StatGeneratorData generatorData = new StatGeneratorData(playerStats);
        final Set<GeneratedPlayerEntry> generatedStats = new HashSet<>();
        for (final StatGenerator statGenerator : this.statGenerators) {
            generatedStats.addAll(statGenerator.generateStats(generatorData));
        }

        return generatedStats;
    }

    protected Set<PlayerEntry> getPlayerEntries(final List<Leaderboard> leaderboards,
                                                final P player,
                                                final ZonedDateTime time,
                                                final Set<Reason> filterReasons,
                                                final boolean includeEmptyEntries) {
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

                    if (includeEmptyEntries) {
                        return new PlayerEntry(
                                save.getLeaderboard(),
                                save.getSaveTime(),
                                -1,
                                -1
                        );
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public Optional<PlayerStats<P>> getPlayerStats(final List<Leaderboard> leaderboards,
                                                   final P player,
                                                   final ZonedDateTime time,
                                                   final Set<Reason> filterReasons,
                                                   final boolean includeEmptyEntries) {
        final Set<PlayerEntry> stats = this.getPlayerEntries(leaderboards, player, time, filterReasons, includeEmptyEntries);
        if (stats.isEmpty()) {
            return Optional.empty();
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
