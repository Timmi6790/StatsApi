package de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard;

import de.timmi6790.mpstats.api.AbstractIntegrationTest;
import de.timmi6790.mpstats.api.utilities.bedrock.BedrockServiceGenerator;
import de.timmi6790.mpstats.api.versions.v1.bedrock.board.BedrockBoardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.game.BedrockGameService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.stat.BedrockStatService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.AbstractLeaderboardServiceTest;

class BedrockLeaderboardServiceTest extends AbstractLeaderboardServiceTest {
    private static final BedrockGameService GAME_SERVICE = BedrockServiceGenerator.generateGameService();
    private static final BedrockStatService STAT_SERVICE = BedrockServiceGenerator.generateStatService();
    private static final BedrockBoardService BOARD_SERVICE = BedrockServiceGenerator.generateBoardService();

    public BedrockLeaderboardServiceTest() {
        super(() -> new BedrockLeaderboardService(
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