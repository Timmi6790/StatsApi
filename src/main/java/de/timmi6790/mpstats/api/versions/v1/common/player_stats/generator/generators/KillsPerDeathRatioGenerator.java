package de.timmi6790.mpstats.api.versions.v1.common.player_stats.generator.generators;

import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.generator.GameBoardStatGenerator;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.generator.StatGeneratorData;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.models.GeneratedPlayerEntry;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.models.PlayerEntry;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class KillsPerDeathRatioGenerator extends GameBoardStatGenerator {
    private static final String DEATHS_WEBSITE_STAT_NAME = "Deaths";

    @Override
    protected Collection<GeneratedPlayerEntry> generateGameBoardStats(final StatGeneratorData generatorData,
                                                                      final Game game,
                                                                      final Board board,
                                                                      final Map<String, PlayerEntry> playerEntries) {
        final PlayerEntry deathsEntry = playerEntries.get(DEATHS_WEBSITE_STAT_NAME);
        if (!this.isPresent(deathsEntry)) {
            return Collections.emptyList();
        }

        return this.getKills(game, playerEntries).map(kills ->
                List.of(
                        new GeneratedPlayerEntry(
                                "KillDeathRatio",
                                game,
                                board,
                                (double) kills / deathsEntry.getScore()
                        )
                )
        ).orElseGet(Collections::emptyList);
    }
}
