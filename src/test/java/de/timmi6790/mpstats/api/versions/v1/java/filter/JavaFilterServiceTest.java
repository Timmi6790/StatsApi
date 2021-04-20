package de.timmi6790.mpstats.api.versions.v1.java.filter;

import de.timmi6790.mpstats.api.AbstractIntegrationTest;
import de.timmi6790.mpstats.api.versions.v1.common.filter.AbstractFilterServiceTest;
import de.timmi6790.mpstats.api.versions.v1.java.board.JavaBoardService;
import de.timmi6790.mpstats.api.versions.v1.java.game.JavaGameService;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.JavaLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.java.player.JavaPlayerService;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaRepositoryPlayer;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.postgres.JavaPlayerPostgresRepository;
import de.timmi6790.mpstats.api.versions.v1.java.stat.JavaStatService;

import java.util.UUID;

class JavaFilterServiceTest extends AbstractFilterServiceTest<JavaRepositoryPlayer, JavaPlayerService> {
    private static final JavaGameService GAME_SERVICE = new JavaGameService(AbstractIntegrationTest.jdbi());
    private static final JavaStatService STAT_SERVICE = new JavaStatService(AbstractIntegrationTest.jdbi());
    private static final JavaBoardService BOARD_SERVICE = new JavaBoardService(AbstractIntegrationTest.jdbi());
    private static final JavaLeaderboardService LEADERBOARD_SERVICE = new JavaLeaderboardService(
            AbstractIntegrationTest.jdbi(),
            GAME_SERVICE,
            STAT_SERVICE,
            BOARD_SERVICE
    );

    private static final JavaPlayerService PLAYER_SERVICE = new JavaPlayerService(new JavaPlayerPostgresRepository(AbstractIntegrationTest.jdbi()));

    public JavaFilterServiceTest() {
        super(() -> new JavaFilterService(
                        AbstractIntegrationTest.jdbi(),
                        PLAYER_SERVICE,
                        LEADERBOARD_SERVICE
                ),
                GAME_SERVICE,
                STAT_SERVICE,
                BOARD_SERVICE
        );
    }

    private UUID generatePlayerUUID() {
        return UUID.randomUUID();
    }

    @Override
    protected JavaRepositoryPlayer generatePlayer() {
        final String playerName = this.generatePlayerName();
        final UUID playerUUID = this.generatePlayerUUID();

        return this.getPlayerService().getPlayerOrCreate(playerName, playerUUID);
    }
}