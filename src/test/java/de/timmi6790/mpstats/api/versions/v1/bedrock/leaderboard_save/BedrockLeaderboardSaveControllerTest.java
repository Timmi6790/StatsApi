package de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard_save;

import de.timmi6790.mpstats.api.versions.v1.bedrock.board.BedrockBoardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.game.BedrockGameService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard.BedrockLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.BedrockPlayerService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockPlayer;
import de.timmi6790.mpstats.api.versions.v1.bedrock.stat.BedrockStatService;
import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.AbstractLeaderboardSaveControllerTest;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.LeaderboardSaveService;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import org.springframework.beans.factory.annotation.Autowired;

class BedrockLeaderboardSaveControllerTest extends AbstractLeaderboardSaveControllerTest<BedrockPlayer> {
    @Autowired
    private BedrockLeaderboardSaveService leaderboardSaveService;
    @Autowired
    private BedrockLeaderboardService leaderboardService;
    @Autowired
    private BedrockGameService gameService;
    @Autowired
    private BedrockStatService statService;
    @Autowired
    private BedrockBoardService boardService;
    @Autowired
    private BedrockPlayerService playerService;

    public BedrockLeaderboardSaveControllerTest() {
        super("/v1/bedrock/leaderboard");
    }

    @Override
    protected LeaderboardSaveService<BedrockPlayer> getSaveService() {
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
    protected PlayerService<BedrockPlayer> getPlayerService() {
        return this.playerService;
    }
}