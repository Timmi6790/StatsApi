package de.timmi6790.mpstats.api.versions.v1.java.leaderboard_save;

import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.AbstractLeaderboardSaveControllerTest;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.LeaderboardSaveService;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import de.timmi6790.mpstats.api.versions.v1.java.board.JavaBoardService;
import de.timmi6790.mpstats.api.versions.v1.java.game.JavaGameService;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.JavaLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.java.player.JavaPlayerService;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
import de.timmi6790.mpstats.api.versions.v1.java.stat.JavaStatService;
import org.springframework.beans.factory.annotation.Autowired;

class JavaLeaderboardSaveControllerTest extends AbstractLeaderboardSaveControllerTest<JavaPlayer> {
    @Autowired
    private JavaLeaderboardSaveService leaderboardSaveService;
    @Autowired
    private JavaLeaderboardService leaderboardService;
    @Autowired
    private JavaGameService gameService;
    @Autowired
    private JavaStatService statService;
    @Autowired
    private JavaBoardService boardService;
    @Autowired
    private JavaPlayerService playerService;

    public JavaLeaderboardSaveControllerTest() {
        super("/v1/java/leaderboard");
    }

    @Override
    protected LeaderboardSaveService<JavaPlayer> getSaveService() {
        return this.leaderboardSaveService;
    }

    @Override
    protected LeaderboardService getLeaderboardService() {
        return this.leaderboardService;
    }

    @Override
    protected GameService getGameService() {
        return this.gameService;
    }

    @Override
    protected StatService getStatService() {
        return this.statService;
    }

    @Override
    protected BoardService getBoardService() {
        return this.boardService;
    }

    @Override
    protected PlayerService<JavaPlayer> getPlayerService() {
        return this.playerService;
    }
}