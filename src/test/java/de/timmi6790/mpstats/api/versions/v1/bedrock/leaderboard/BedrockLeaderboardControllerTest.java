package de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard;

import de.timmi6790.mpstats.api.versions.v1.bedrock.board.BedrockBoardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.game.BedrockGameService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.stat.BedrockStatService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.AbstractLeaderboardControllerTest;
import org.springframework.beans.factory.annotation.Autowired;

class BedrockLeaderboardControllerTest extends AbstractLeaderboardControllerTest<BedrockLeaderboardService, BedrockGameService, BedrockStatService, BedrockBoardService> {
    @Autowired
    private BedrockLeaderboardService leaderboardService;
    @Autowired
    private BedrockGameService gameService;
    @Autowired
    private BedrockStatService statService;
    @Autowired
    private BedrockBoardService boardService;

    public BedrockLeaderboardControllerTest() {
        super("/v1/bedrock/leaderboard");
    }

    @Override
    protected BedrockLeaderboardService getLeaderboardService() {
        return this.leaderboardService;
    }

    @Override
    protected BedrockGameService getGameService() {
        return this.gameService;
    }

    @Override
    protected BedrockStatService getStatService() {
        return this.statService;
    }

    @Override
    protected BedrockBoardService getBoardService() {
        return this.boardService;
    }
}