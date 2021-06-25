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

public class GemsPerGameGenerator extends GameBoardStatGenerator {
    private static final String GEMS_EARNED_WEBSITE_STAT_NAME = "GemsEarned";

    @Override
    protected Collection<GeneratedPlayerEntry> generateGameBoardStats(final StatGeneratorData generatorData,
                                                                      final Game game,
                                                                      final Board board,
                                                                      final Map<String, PlayerEntry> playerEntries) {
        final PlayerEntry gemsEntry = playerEntries.get(GEMS_EARNED_WEBSITE_STAT_NAME);
        if (!this.isPresent(gemsEntry)) {
            return Collections.emptyList();
        }

        return this.getGamesPlayed(game, playerEntries).map(gamesPlayed ->
                List.of(
                        new GeneratedPlayerEntry(
                                "GemsPerGame",
                                game,
                                board,
                                (double) gemsEntry.getScore() / gamesPlayed
                        )
                )
        ).orElseGet(Collections::emptyList);
    }
}