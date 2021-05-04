package de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard_save;

import de.timmi6790.mpstats.api.AbstractIntegrationTest;
import de.timmi6790.mpstats.api.utilities.bedrock.BedrockServiceGenerator;
import de.timmi6790.mpstats.api.versions.v1.bedrock.board.BedrockBoardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.game.BedrockGameService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard.BedrockLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockPlayer;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockRepositoryPlayer;
import de.timmi6790.mpstats.api.versions.v1.bedrock.stat.BedrockStatService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.AbstractLeaderboardSaveServiceTest;

import static org.assertj.core.api.Assertions.assertThat;

class BedrockLeaderboardSaveServiceTest extends AbstractLeaderboardSaveServiceTest<BedrockPlayer, BedrockRepositoryPlayer> {
    private static final BedrockGameService GAME_SERVICE = BedrockServiceGenerator.generateGameService();
    private static final BedrockStatService STAT_SERVICE = BedrockServiceGenerator.generateStatService();
    private static final BedrockBoardService BOARD_SERVICE = BedrockServiceGenerator.generateBoardService();
    private static final BedrockLeaderboardService LEADERBOARD_SERVICE = new BedrockLeaderboardService(
            AbstractIntegrationTest.jdbi(),
            GAME_SERVICE,
            STAT_SERVICE,
            BOARD_SERVICE
    );

    protected BedrockLeaderboardSaveServiceTest() {
        super(
                new BedrockLeaderboardSaveService(
                        BedrockServiceGenerator.generatePlayerService(),
                        AbstractIntegrationTest.jdbi()
                ),
                LEADERBOARD_SERVICE,
                GAME_SERVICE,
                STAT_SERVICE,
                BOARD_SERVICE
        );
    }

    @Override
    protected void verifyPlayer(final BedrockPlayer insertedPlayer, final BedrockRepositoryPlayer repositoryPlayer) {
        assertThat(insertedPlayer.getPlayerName()).isEqualTo(repositoryPlayer.getPlayerName());
    }
}