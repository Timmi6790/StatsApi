package de.timmi6790.mpstats.api.utilities;

import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;

public class LeaderboardUtilities {
    public static Leaderboard generateLeaderboard(final LeaderboardService leaderboardService,
                                                  final GameService gameService,
                                                  final StatService statService,
                                                  final BoardService boardService) {
        final Game game = GameUtilities.generateGame(gameService);
        return generateLeaderboard(leaderboardService, statService, boardService, game);
    }

    public static Leaderboard generateLeaderboard(final LeaderboardService leaderboardService,
                                                  final StatService statService,
                                                  final BoardService boardService,
                                                  final Game game) {
        final Stat stat = StatUtilities.generateStat(statService);
        final Board board = BoardUtilities.generateBoard(boardService);
        final boolean deprecated = true;

        return generateLeaderboard(leaderboardService, game, stat, board);
    }

    public static Leaderboard generateLeaderboard(final LeaderboardService leaderboardService,
                                                  final Game game,
                                                  final Stat stat,
                                                  final Board board) {
        final boolean deprecated = true;

        return leaderboardService.getLeaderboardOrCreate(game, stat, board, deprecated);
    }
}
