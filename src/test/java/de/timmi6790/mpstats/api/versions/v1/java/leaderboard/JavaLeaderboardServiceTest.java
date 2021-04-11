package de.timmi6790.mpstats.api.versions.v1.java.leaderboard;

import de.timmi6790.mpstats.api.AbstractIntegrationTest;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.AbstractLeaderboardServiceTest;
import de.timmi6790.mpstats.api.versions.v1.java.board.JavaBoardService;
import de.timmi6790.mpstats.api.versions.v1.java.game.JavaGameService;
import de.timmi6790.mpstats.api.versions.v1.java.stat.JavaStatService;

class JavaLeaderboardServiceTest extends AbstractLeaderboardServiceTest {
    private static final JavaGameService JAVA_GAME_SERVICE = new JavaGameService(AbstractIntegrationTest.jdbi());
    private static final JavaStatService JAVA_STAT_SERVICE = new JavaStatService(AbstractIntegrationTest.jdbi());
    private static final JavaBoardService JAVA_BOARD_SERVICE = new JavaBoardService(AbstractIntegrationTest.jdbi());

    public JavaLeaderboardServiceTest() {
        super(() -> new JavaLeaderboardService(
                        AbstractIntegrationTest.jdbi(),
                        JAVA_GAME_SERVICE,
                        JAVA_STAT_SERVICE,
                        JAVA_BOARD_SERVICE
                ),
                JAVA_GAME_SERVICE,
                JAVA_STAT_SERVICE,
                JAVA_BOARD_SERVICE
        );
    }
}