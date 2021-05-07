package de.timmi6790.mpstats.api.versions.v1.java.leaderboard;


import de.timmi6790.mpstats.api.utilities.java.JavaServiceGenerator;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.AbstractLeaderboardTest;
import de.timmi6790.mpstats.api.versions.v1.java.board.JavaBoardService;
import de.timmi6790.mpstats.api.versions.v1.java.game.JavaGameService;
import de.timmi6790.mpstats.api.versions.v1.java.stat.JavaStatService;

public class JavaLeaderboardTest extends AbstractLeaderboardTest {
    private static final JavaGameService gameService = JavaServiceGenerator.generateGameService();
    private static final JavaStatService statService = JavaServiceGenerator.generateStatService();
    private static final JavaBoardService boardService = JavaServiceGenerator.generateBoardService();

    public JavaLeaderboardTest() {
        super(
                new JavaLeaderboardService(
                        JavaServiceGenerator.getJdbi(),
                        gameService,
                        statService,
                        boardService
                ),
                gameService,
                boardService,
                statService
        );
    }
}
