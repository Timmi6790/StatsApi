package de.timmi6790.mpstats.api.versions.v1.common.player_stats.models;

import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GeneratedPlayerEntry {
    private final String cleanStatName;
    private final Game game;
    private final Board board;
    private final Number score;

    public GeneratedPlayerEntry(final String cleanStatName, final Number score, final Leaderboard leaderboard) {
        this(
                cleanStatName,
                leaderboard.getGame(),
                leaderboard.getBoard(),
                score
        );
    }
}
