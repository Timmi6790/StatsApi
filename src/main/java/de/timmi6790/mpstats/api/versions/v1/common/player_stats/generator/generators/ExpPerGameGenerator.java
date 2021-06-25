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

public class ExpPerGameGenerator extends GameBoardStatGenerator {
    private static final String EXP_EARNED_WEBSITE_STAT_NAME = "ExpEarned";

    @Override
    protected Collection<GeneratedPlayerEntry> generateGameBoardStats(final StatGeneratorData generatorData,
                                                                      final Game game,
                                                                      final Board board,
                                                                      final Map<String, PlayerEntry> playerEntries) {
        final PlayerEntry expEntry = playerEntries.get(EXP_EARNED_WEBSITE_STAT_NAME);
        if (!this.isPresent(expEntry)) {
            return Collections.emptyList();
        }

        return this.getGamesPlayed(game, playerEntries).map(gamesPlayed ->
                List.of(
                        new GeneratedPlayerEntry(
                                "ExpPerGame",
                                game,
                                board,
                                (double) expEntry.getScore() / gamesPlayed
                        )
                )
        ).orElseGet(Collections::emptyList);
    }
}
