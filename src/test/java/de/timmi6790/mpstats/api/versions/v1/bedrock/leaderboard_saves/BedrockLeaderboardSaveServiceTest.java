package de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard_saves;

import de.timmi6790.mpstats.api.AbstractIntegrationTest;
import de.timmi6790.mpstats.api.versions.v1.bedrock.board.BedrockBoardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.game.BedrockGameService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard.BedrockLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.BedrockPlayerService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockRepositoryPlayer;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.postgres.BedrockPlayerPostgresRepository;
import de.timmi6790.mpstats.api.versions.v1.bedrock.stat.BedrockStatService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_saves.AbstractLeaderboardSaveServiceTest;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;

import static org.assertj.core.api.Assertions.assertThat;

class BedrockLeaderboardSaveServiceTest extends AbstractLeaderboardSaveServiceTest<Player, BedrockRepositoryPlayer> {
    private static final BedrockGameService GAME_SERVICE = new BedrockGameService(AbstractIntegrationTest.jdbi());
    private static final BedrockStatService STAT_SERVICE = new BedrockStatService(AbstractIntegrationTest.jdbi());
    private static final BedrockBoardService BOARD_SERVICE = new BedrockBoardService(AbstractIntegrationTest.jdbi());
    private static final BedrockLeaderboardService LEADERBOARD_SERVICE = new BedrockLeaderboardService(
            AbstractIntegrationTest.jdbi(),
            GAME_SERVICE,
            STAT_SERVICE,
            BOARD_SERVICE
    );

    protected BedrockLeaderboardSaveServiceTest() {
        super(
                new BedrockLeaderboardSaveService(
                        new BedrockPlayerService(
                                new BedrockPlayerPostgresRepository(AbstractIntegrationTest.jdbi()
                                )
                        ),
                        AbstractIntegrationTest.jdbi()
                ),
                LEADERBOARD_SERVICE,
                GAME_SERVICE,
                STAT_SERVICE,
                BOARD_SERVICE
        );
    }

    @Override
    protected void verifyPlayer(final Player insertedPlayer, final BedrockRepositoryPlayer repositoryPlayer) {
        assertThat(insertedPlayer.getPlayerName()).isEqualTo(repositoryPlayer.getPlayerName());
    }
}