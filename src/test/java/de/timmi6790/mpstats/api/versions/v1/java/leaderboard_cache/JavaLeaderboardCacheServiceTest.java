package de.timmi6790.mpstats.api.versions.v1.java.leaderboard_cache;

import de.timmi6790.mpstats.api.utilities.PlayerUtilities;
import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_cache.AbstractLeaderboardCacheServiceTest;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_cache.LeaderboardCacheService;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import de.timmi6790.mpstats.api.versions.v1.java.board.JavaBoardService;
import de.timmi6790.mpstats.api.versions.v1.java.game.JavaGameService;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.JavaLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
import de.timmi6790.mpstats.api.versions.v1.java.stat.JavaStatService;
import org.springframework.beans.factory.annotation.Autowired;

class JavaLeaderboardCacheServiceTest extends AbstractLeaderboardCacheServiceTest<JavaPlayer> {
    @Autowired
    private JavaGameService gameService;
    @Autowired
    private JavaLeaderboardCacheService leaderboardCacheService;
    @Autowired
    private JavaStatService statService;
    @Autowired
    private JavaBoardService boardService;
    @Autowired
    private JavaLeaderboardService leaderboardService;

    @Override
    protected LeaderboardCacheService<JavaPlayer> getLeaderboardCacheService() {
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
    protected JavaPlayer generatePlayer() {
        return new JavaPlayer(
                PlayerUtilities.generatePlayerName(),
                PlayerUtilities.generatePlayerUUID()
        );
    }
}