package de.timmi6790.mpstats.api.versions.v1.bedrock.filter;

import de.timmi6790.mpstats.api.AbstractIntegrationTest;
import de.timmi6790.mpstats.api.utilities.bedrock.BedrockServiceGenerator;
import de.timmi6790.mpstats.api.versions.v1.bedrock.board.BedrockBoardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.game.BedrockGameService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard.BedrockLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.BedrockPlayerService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockRepositoryPlayer;
import de.timmi6790.mpstats.api.versions.v1.bedrock.stat.BedrockStatService;
import de.timmi6790.mpstats.api.versions.v1.common.filter.AbstractFilterServiceTest;

class BedrockFilterServiceTest extends AbstractFilterServiceTest<BedrockRepositoryPlayer, BedrockPlayerService> {
    private static final BedrockGameService GAME_SERVICE = BedrockServiceGenerator.generateGameService();
    private static final BedrockStatService STAT_SERVICE = BedrockServiceGenerator.generateStatService();
    private static final BedrockBoardService BOARD_SERVICE = BedrockServiceGenerator.generateBoardService();
    private static final BedrockLeaderboardService LEADERBOARD_SERVICE = new BedrockLeaderboardService(
            AbstractIntegrationTest.jdbi(),
            GAME_SERVICE,
            STAT_SERVICE,
            BOARD_SERVICE
    );

    private static final BedrockPlayerService PLAYER_SERVICE = BedrockServiceGenerator.generatePlayerService();

    public BedrockFilterServiceTest() {
        super(() -> new BedrockFilterService(
                        AbstractIntegrationTest.jdbi(),
                        PLAYER_SERVICE,
                        LEADERBOARD_SERVICE
                ),
                GAME_SERVICE,
                STAT_SERVICE,
                BOARD_SERVICE
        );
    }
}