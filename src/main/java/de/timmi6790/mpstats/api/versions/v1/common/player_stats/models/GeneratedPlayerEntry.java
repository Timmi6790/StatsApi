package de.timmi6790.mpstats.api.versions.v1.common.player_stats.models;

import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Data
@Log4j2
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

    public GeneratedPlayerEntry(final String cleanStatName, final Game game, final Board board, final Number score) {
        this.cleanStatName = cleanStatName;
        this.game = game;
        this.board = board;

        // We should never return a nan value
        if (this.isNan(score)) {
            log.debug(
                    "Found nan value for {}-{}-{}",
                    game.getGameName(),
                    cleanStatName,
                    board.getBoardName()
            );
            this.score = 0;
        } else {
            this.score = score;
        }
    }

    private boolean isNan(final Number number) {
        if (number instanceof final Float floatNumber) {
            return floatNumber.isNaN();
        } else if (number instanceof final Double doubleNumber) {
            return doubleNumber.isNaN();
        }

        return false;
    }
}
