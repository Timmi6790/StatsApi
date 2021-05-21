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

public class KillAssistsRatioGenerator extends GameBoardStatGenerator {
    private static final String ASSISTS_WEBSITE_STAT_NAME = "Assists";

    @Override
    protected Collection<GeneratedPlayerEntry> generateGameBoardStats(final StatGeneratorData generatorData,
                                                                      final Game game,
                                                                      final Board board,
                                                                      final Map<String, PlayerEntry> playerEntries) {
        final PlayerEntry assistsEntry = playerEntries.get(ASSISTS_WEBSITE_STAT_NAME);
        if (!this.isPresent(assistsEntry)) {
            return Collections.emptyList();
        }

        return this.getKills(game, playerEntries).map(kills ->
                List.of(
                        new GeneratedPlayerEntry(
                                "KillAssistRatio",
                                game,
                                board,
                                (double) kills / assistsEntry.getScore()
                        )
                )
        ).orElseGet(Collections::emptyList);
    }
}