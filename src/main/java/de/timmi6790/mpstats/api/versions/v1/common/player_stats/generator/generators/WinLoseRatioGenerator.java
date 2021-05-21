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

public class WinLoseRatioGenerator extends GameBoardStatGenerator {
    @Override
    protected Collection<GeneratedPlayerEntry> generateGameBoardStats(final StatGeneratorData generatorData,
                                                                      final Game game,
                                                                      final Board board,
                                                                      final Map<String, PlayerEntry> playerEntries) {
        final PlayerEntry lossesEntry = playerEntries.get(LOSSES_WEBSITE_STAT_NAME);
        if (!this.isPresent(lossesEntry)) {
            return Collections.emptyList();
        }

        final PlayerEntry winsEntry = playerEntries.get(WINS_WEBSITE_STAT_NAME);
        if (this.isPresent(winsEntry)) {
            return List.of(
                    new GeneratedPlayerEntry(
                            "WinLoseRatio",
                            game,
                            board,
                            (double) winsEntry.getScore() / lossesEntry.getScore()
                    )
            );
        }
        return Collections.emptyList();
    }
}