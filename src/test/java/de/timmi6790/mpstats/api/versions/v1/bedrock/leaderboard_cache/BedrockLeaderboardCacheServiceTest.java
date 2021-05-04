package de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard_cache;

import de.timmi6790.mpstats.api.utilities.PlayerUtilities;
import de.timmi6790.mpstats.api.versions.v1.bedrock.board.BedrockBoardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.game.BedrockGameService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard.BedrockLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockPlayer;
import de.timmi6790.mpstats.api.versions.v1.bedrock.stat.BedrockStatService;
import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_cache.AbstractLeaderboardCacheServiceTest;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_cache.LeaderboardCacheService;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import org.springframework.beans.factory.annotation.Autowired;

class BedrockLeaderboardCacheServiceTest extends AbstractLeaderboardCacheServiceTest<BedrockPlayer> {
    @Autowired
    private BedrockGameService gameService;
    @Autowired
    private BedrockLeaderboardCacheService leaderboardCacheService;
    @Autowired
    private BedrockStatService statService;
    @Autowired
    private BedrockBoardService boardService;
    @Autowired
    private BedrockLeaderboardService leaderboardService;

    @Override
    protected LeaderboardCacheService<BedrockPlayer> getLeaderboardCacheService() {
        return this.leaderboardCacheService;
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
    protected BedrockPlayer generatePlayer() {
        return new BedrockPlayer(
                PlayerUtilities.generatePlayerName()
        );
    }
}