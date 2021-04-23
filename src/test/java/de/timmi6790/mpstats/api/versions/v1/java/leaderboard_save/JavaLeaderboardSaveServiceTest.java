package de.timmi6790.mpstats.api.versions.v1.java.leaderboard_save;

import de.timmi6790.mpstats.api.AbstractIntegrationTest;
import de.timmi6790.mpstats.api.utilities.java.JavaServiceGenerator;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.AbstractLeaderboardSaveServiceTest;
import de.timmi6790.mpstats.api.versions.v1.java.board.JavaBoardService;
import de.timmi6790.mpstats.api.versions.v1.java.game.JavaGameService;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.JavaLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaRepositoryPlayer;
import de.timmi6790.mpstats.api.versions.v1.java.stat.JavaStatService;

import static org.assertj.core.api.Assertions.assertThat;

class JavaLeaderboardSaveServiceTest extends AbstractLeaderboardSaveServiceTest<JavaPlayer, JavaRepositoryPlayer> {
    private static final JavaGameService GAME_SERVICE = JavaServiceGenerator.generateGameService();
    private static final JavaStatService STAT_SERVICE = JavaServiceGenerator.generateStatService();
    private static final JavaBoardService BOARD_SERVICE = JavaServiceGenerator.generateBoardService();
    private static final JavaLeaderboardService LEADERBOARD_SERVICE = new JavaLeaderboardService(
            AbstractIntegrationTest.jdbi(),
            GAME_SERVICE,
            STAT_SERVICE,
            BOARD_SERVICE
    );

    protected JavaLeaderboardSaveServiceTest() {
        super(
                new JavaLeaderboardSaveService(
                        JavaServiceGenerator.generatePlayerService(),
                        AbstractIntegrationTest.jdbi()
                ),
                LEADERBOARD_SERVICE,
                GAME_SERVICE,
                STAT_SERVICE,
                BOARD_SERVICE
        );
    }

    @Override
    protected void verifyPlayer(final JavaPlayer insertedPlayer, final JavaRepositoryPlayer repositoryPlayer) {
        assertThat(insertedPlayer.getPlayerName()).isEqualTo(repositoryPlayer.getPlayerName());
        assertThat(insertedPlayer.getPlayerUUID()).isEqualTo(repositoryPlayer.getPlayerUUID());
    }
}