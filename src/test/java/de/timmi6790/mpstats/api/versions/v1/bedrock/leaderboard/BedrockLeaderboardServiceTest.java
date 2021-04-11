package de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard;

import de.timmi6790.mpstats.api.AbstractIntegrationTest;
import de.timmi6790.mpstats.api.versions.v1.bedrock.board.BedrockBoardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.game.BedrockGameService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.stat.BedrockStatService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.AbstractLeaderboardServiceTest;

class BedrockLeaderboardServiceTest extends AbstractLeaderboardServiceTest {
    private static final BedrockGameService BEDROCK_GAME_SERVICE = new BedrockGameService(AbstractIntegrationTest.jdbi());
    private static final BedrockStatService BEDROCK_STAT_SERVICE = new BedrockStatService(AbstractIntegrationTest.jdbi());
    private static final BedrockBoardService BEDROCK_BOARD_SERVICE = new BedrockBoardService(AbstractIntegrationTest.jdbi());

    public BedrockLeaderboardServiceTest() {
        super(() -> new BedrockLeaderboardService(
                        AbstractIntegrationTest.jdbi(),
                        BEDROCK_GAME_SERVICE,
                        BEDROCK_STAT_SERVICE,
                        BEDROCK_BOARD_SERVICE
                ),
                BEDROCK_GAME_SERVICE,
                BEDROCK_STAT_SERVICE,
                BEDROCK_BOARD_SERVICE
        );
    }
}