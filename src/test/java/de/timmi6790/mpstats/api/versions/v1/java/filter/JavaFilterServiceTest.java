package de.timmi6790.mpstats.api.versions.v1.java.filter;

import de.timmi6790.mpstats.api.AbstractIntegrationTest;
import de.timmi6790.mpstats.api.versions.v1.common.filter.AbstractFilterServiceTest;
import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models.Filter;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.java.board.JavaBoardService;
import de.timmi6790.mpstats.api.versions.v1.java.game.JavaGameService;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.JavaLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.java.player.JavaPlayerService;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.postgres.JavaPlayerPostgresRepository;
import de.timmi6790.mpstats.api.versions.v1.java.stat.JavaStatService;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static de.timmi6790.mpstats.api.utilities.PlayerUtilities.generatePlayerUUID;
import static org.assertj.core.api.Assertions.assertThat;

class JavaFilterServiceTest extends AbstractFilterServiceTest<JavaPlayer, JavaPlayerService, JavaFilterService> {
    private static final JavaGameService GAME_SERVICE = new JavaGameService(AbstractIntegrationTest.jdbi());
    private static final JavaStatService STAT_SERVICE = new JavaStatService(AbstractIntegrationTest.jdbi());
    private static final JavaBoardService BOARD_SERVICE = new JavaBoardService(AbstractIntegrationTest.jdbi());
    private static final JavaLeaderboardService LEADERBOARD_SERVICE = new JavaLeaderboardService(
            AbstractIntegrationTest.jdbi(),
            GAME_SERVICE,
            STAT_SERVICE,
            BOARD_SERVICE
    );

    private static final JavaPlayerService PLAYER_SERVICE = new JavaPlayerService(new JavaPlayerPostgresRepository(AbstractIntegrationTest.jdbi()), new SimpleMeterRegistry());

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

    @Test
    void isFiltered_playerUUID_leaderboard_time_equals_filter_start() {
        final Filter<JavaPlayer> filter = this.generateFilter();

        final boolean found = this.filterService.isFiltered(
                filter.getPlayer().getUuid(),
                filter.getLeaderboard(),
                filter.getFilterDuration().getStart()
        );
        assertThat(found).isTrue();
    }

    @Test
    void isFiltered_playerUUID_leaderboard_time_equals_filter_end() {
        final Filter<JavaPlayer> filter = this.generateFilter();

        final boolean found = this.filterService.isFiltered(
                filter.getPlayer().getUuid(),
                filter.getLeaderboard(),
                filter.getFilterDuration().getEnd()
        );
        assertThat(found).isTrue();
    }

    @Test
    void isFiltered_playerUUID_leaderboard() {
        final Filter<JavaPlayer> filter = this.generateFilter();

        final boolean found = this.filterService.isFiltered(
                filter.getPlayer().getUuid(),
                filter.getLeaderboard()
        );
        assertThat(found).isTrue();
    }

    @Test
    void isFiltered_playerUUID_leaderboard_not_found() {
        final Leaderboard leaderboard = this.generateLeaderboard();
        final UUID playerUUID = generatePlayerUUID();

        final boolean notFound = this.filterService.isFiltered(
                playerUUID,
                leaderboard
        );
        assertThat(notFound).isFalse();
    }
}