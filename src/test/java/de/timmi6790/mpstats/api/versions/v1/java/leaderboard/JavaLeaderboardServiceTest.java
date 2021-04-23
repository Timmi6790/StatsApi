package de.timmi6790.mpstats.api.versions.v1.java.leaderboard;

import de.timmi6790.mpstats.api.AbstractIntegrationTest;
import de.timmi6790.mpstats.api.utilities.java.JavaServiceGenerator;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.AbstractLeaderboardServiceTest;
import de.timmi6790.mpstats.api.versions.v1.java.board.JavaBoardService;
import de.timmi6790.mpstats.api.versions.v1.java.game.JavaGameService;
import de.timmi6790.mpstats.api.versions.v1.java.stat.JavaStatService;

class JavaLeaderboardServiceTest extends AbstractLeaderboardServiceTest {
    private static final JavaGameService GAME_SERVICE = JavaServiceGenerator.generateGameService();
    private static final JavaStatService STAT_SERVICE = JavaServiceGenerator.generateStatService();
    private static final JavaBoardService BOARD_SERVICE = JavaServiceGenerator.generateBoardService();

    public JavaLeaderboardServiceTest() {
        super(() -> new JavaLeaderboardService(
                        AbstractIntegrationTest.jdbi(),
                        GAME_SERVICE,
                        STAT_SERVICE,
                        BOARD_SERVICE
                ),
                GAME_SERVICE,
                STAT_SERVICE,
                BOARD_SERVICE
        );
    }
}