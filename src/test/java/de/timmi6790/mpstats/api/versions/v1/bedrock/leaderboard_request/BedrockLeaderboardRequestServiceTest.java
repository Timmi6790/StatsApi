package de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard_request;

import de.timmi6790.mpstats.api.Config;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_request.AbstractLeaderboardRequestServiceTest;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class BedrockLeaderboardRequestServiceTest extends AbstractLeaderboardRequestServiceTest<Player> {
    private static final String BASE_PATH = "leaderboard_request/bedrock/";

    public BedrockLeaderboardRequestServiceTest() {
        super(new BedrockLeaderboardRequestService(new Config()));
    }

    private void validatePlayerData(final LeaderboardEntry<Player> data,
                                    final String requiredName,
                                    final long requiredScore) {
        assertThat(data.getPlayer().getPlayerName()).isEqualTo(requiredName);
        assertThat(data.getScore()).isEqualTo(requiredScore);
    }

    @Test
    void retrieveLeaderboard_empty() {
        final Optional<List<LeaderboardEntry<Player>>> parsedLeaderboardOpt = this.retrieveLeaderboard(BASE_PATH + "0_entries");
        assertThat(parsedLeaderboardOpt).isEmpty();
    }

    @Test
    void retrieveLeaderboard_big() {
        final Optional<List<LeaderboardEntry<Player>>> parsedLeaderboardOpt = this.retrieveLeaderboard(BASE_PATH + "100_entries");
        assertThat(parsedLeaderboardOpt).isPresent();

        final List<LeaderboardEntry<Player>> parsedLeaderboard = parsedLeaderboardOpt.get();
        assertThat(parsedLeaderboard)
                .hasSize(100);

        this.validatePlayerData(
                parsedLeaderboard.get(0),
                "forevrrfury",
                1_084L
        );

        this.validatePlayerData(
                parsedLeaderboard.get(1),
                "bomblobbers",
                1_012L
        );

        this.validatePlayerData(
                parsedLeaderboard.get(2),
                "rainn3959",
                754L
        );

        this.validatePlayerData(
                parsedLeaderboard.get(3),
                "itzselenasavage",
                742L
        );

        this.validatePlayerData(
                parsedLeaderboard.get(4),
                "fiendishlytm",
                593L
        );

        this.validatePlayerData(
                parsedLeaderboard.get(49),
                "thunder pro 573",
                79L
        );

        this.validatePlayerData(
                parsedLeaderboard.get(50),
                "Potatoking726",
                79L
        );

        this.validatePlayerData(
                parsedLeaderboard.get(99),
                "endrmine",
                58L
        );
    }
}