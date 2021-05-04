package de.timmi6790.mpstats.api.versions.v1.java.leaderboard;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.AbstractLeaderboardControllerTest;
import de.timmi6790.mpstats.api.versions.v1.java.board.JavaBoardService;
import de.timmi6790.mpstats.api.versions.v1.java.game.JavaGameService;
import de.timmi6790.mpstats.api.versions.v1.java.stat.JavaStatService;
import org.springframework.beans.factory.annotation.Autowired;

class JavaLeaderboardControllerTest extends AbstractLeaderboardControllerTest<JavaLeaderboardService, JavaGameService, JavaStatService, JavaBoardService> {
    @Autowired
    private JavaLeaderboardService leaderboardService;
    @Autowired
    private JavaGameService gameService;
    @Autowired
    private JavaStatService statService;
    @Autowired
    private JavaBoardService boardService;

    public JavaLeaderboardControllerTest() {
        super("/v1/java/leaderboard");
    }

    @Override
    protected JavaLeaderboardService getLeaderboardService() {
        return this.leaderboardService;
    }

    @Override
    protected JavaGameService getGameService() {
        return this.gameService;
    }

    @Override
    protected JavaStatService getStatService() {
        return this.statService;
    }

    @Override
    protected JavaBoardService getBoardService() {
        return this.boardService;
    }
}