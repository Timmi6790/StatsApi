package de.timmi6790.mpstats.api.versions.v1.common.player_stats.generator;

import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.models.GeneratedPlayerEntry;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.models.PlayerEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class GameBoardStatGenerator extends BaseStatGenerator {
    protected abstract Collection<GeneratedPlayerEntry> generateGameBoardStats(StatGeneratorData generatorData,
                                                                               Game game,
                                                                               Board board,
                                                                               Map<String, PlayerEntry> playerEntries);

    @Override
    public Collection<GeneratedPlayerEntry> generateStats(final StatGeneratorData generatorData) {
        final List<GeneratedPlayerEntry> entries = new ArrayList<>();
        for (final Game game : generatorData.getGames()) {
            for (final Board board : generatorData.getBoards(game)) {
                final Map<String, PlayerEntry> playerEntries = generatorData.getPlayerEntries(game, board);
                entries.addAll(this.generateGameBoardStats(generatorData, game, board, playerEntries));
            }
        }

        return entries;
    }
}
