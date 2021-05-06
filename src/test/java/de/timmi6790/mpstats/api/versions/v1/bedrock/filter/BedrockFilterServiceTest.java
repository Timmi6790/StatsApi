package de.timmi6790.mpstats.api.versions.v1.bedrock.filter;

import de.timmi6790.mpstats.api.AbstractIntegrationTest;
import de.timmi6790.mpstats.api.utilities.bedrock.BedrockServiceGenerator;
import de.timmi6790.mpstats.api.versions.v1.bedrock.board.BedrockBoardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.game.BedrockGameService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard.BedrockLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.BedrockPlayerService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockPlayer;
import de.timmi6790.mpstats.api.versions.v1.bedrock.stat.BedrockStatService;
import de.timmi6790.mpstats.api.versions.v1.common.filter.AbstractFilterServiceTest;
import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models.Filter;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import org.junit.jupiter.api.Test;

import static de.timmi6790.mpstats.api.utilities.PlayerUtilities.generatePlayerName;
import static org.assertj.core.api.Assertions.assertThat;

class BedrockFilterServiceTest extends AbstractFilterServiceTest<BedrockPlayer, BedrockPlayerService, BedrockFilterService> {
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

    @Test
    void isFiltered_playerName_leaderboard_time_equals_filter_start() {
        final Filter<BedrockPlayer> filter = this.generateFilter();

        final boolean found = this.filterService.isFiltered(
                filter.getPlayer().getName(),
                filter.getLeaderboard(),
                filter.getStart()
        );
        assertThat(found).isTrue();
    }

    @Test
    void isFiltered_playerName_leaderboard_time_equals_filter_end() {
        final Filter<BedrockPlayer> filter = this.generateFilter();

        final boolean found = this.filterService.isFiltered(
                filter.getPlayer().getName(),
                filter.getLeaderboard(),
                filter.getEnd()
        );
        assertThat(found).isTrue();
    }


    @Test
    void isFiltered_playerName_leaderboard() {
        final Filter<BedrockPlayer> filter = this.generateFilter();

        final boolean found = this.filterService.isFiltered(
                filter.getPlayer().getName(),
                filter.getLeaderboard()
        );
        assertThat(found).isTrue();
    }

    @Test
    void isFiltered_playerName_leaderboard_not_found() {
        final Leaderboard leaderboard = this.generateLeaderboard();
        final String playerName = generatePlayerName();

        final boolean notFound = this.filterService.isFiltered(
                playerName,
                leaderboard
        );
        assertThat(notFound).isFalse();
    }
}