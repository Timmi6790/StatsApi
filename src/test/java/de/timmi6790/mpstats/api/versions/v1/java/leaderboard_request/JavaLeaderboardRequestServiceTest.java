package de.timmi6790.mpstats.api.versions.v1.java.leaderboard_request;

import de.timmi6790.mpstats.api.Config;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_request.AbstractLeaderboardRequestServiceTest;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_request.models.WebLeaderboard;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JavaLeaderboardRequestServiceTest extends AbstractLeaderboardRequestServiceTest<JavaPlayer> {
    private static final String BASE_PATH = "leaderboard_request/java/";

    public JavaLeaderboardRequestServiceTest() {
        super(new JavaLeaderboardRequestService(new Config()));
    }

    private void validatePlayerData(final WebLeaderboard<JavaPlayer> data,
                                    final String requiredName,
                                    final String requiredUUID,
                                    final long requiredScore) {
        assertThat(data.getScore()).isEqualTo(requiredScore);

        final JavaPlayer player = data.getPlayer();
        assertThat(player.getPlayerName()).isEqualTo(requiredName);
        assertThat(player.getPlayerUUID()).isEqualTo(UUID.fromString(requiredUUID));
    }

    @Test
    void retrieveLeaderboard_empty() {
        final Optional<List<WebLeaderboard<JavaPlayer>>> parsedLeaderboardOpt = this.retrieveLeaderboard(BASE_PATH + "0_entries");
        assertThat(parsedLeaderboardOpt).isEmpty();
    }

    @Test
    void retrieveLeaderboard_small() {
        final Optional<List<WebLeaderboard<JavaPlayer>>> parsedLeaderboardOpt = this.retrieveLeaderboard(BASE_PATH + "67_entries");
        assertThat(parsedLeaderboardOpt).isPresent();

        final List<WebLeaderboard<JavaPlayer>> parsedLeaderboard = parsedLeaderboardOpt.get();
        assertThat(parsedLeaderboard)
                .hasSize(67);

        this.validatePlayerData(
                parsedLeaderboard.get(0),
                "cocoalinaa",
                "af033982-0fea-478c-8a71-9a3e343dd531",
                2L
        );

        this.validatePlayerData(
                parsedLeaderboard.get(42),
                "Apsungi",
                "891a8852-e10e-4fe0-b79d-8c1b59716d65",
                1L
        );

        this.validatePlayerData(
                parsedLeaderboard.get(66),
                "alexl2810",
                "ba358fac-70bf-4ec8-8ba3-60cf440dcef2",
                1L
        );
    }

    @Test
    void retrieveLeaderboard_big() {
        final Optional<List<WebLeaderboard<JavaPlayer>>> parsedLeaderboardOpt = this.retrieveLeaderboard(BASE_PATH + "1000_entries");
        assertThat(parsedLeaderboardOpt).isPresent();

        final List<WebLeaderboard<JavaPlayer>> parsedLeaderboard = parsedLeaderboardOpt.get();
        assertThat(parsedLeaderboard)
                .hasSize(1_000);

        this.validatePlayerData(
                parsedLeaderboard.get(0),
                "Phinary",
                "b33207e2-0dc5-4cbd-b3ee-6c860727f722",
                42_100_726_053L
        );

        this.validatePlayerData(
                parsedLeaderboard.get(1),
                "Mysticate",
                "5c359761-d55b-43a4-9b75-2cc64f8d027f",
                12_885_842_896L
        );

        this.validatePlayerData(
                parsedLeaderboard.get(2),
                "LCastr0",
                "a68b8d0e-24be-4851-afa1-9e4c506b3e92",
                1_010_133_249L
        );

        this.validatePlayerData(
                parsedLeaderboard.get(3),
                "Relyh",
                "68b61e3c-4be0-4c0c-8897-6a8d3703fe9a",
                1_001_294_815L
        );

        this.validatePlayerData(
                parsedLeaderboard.get(4),
                "B2_mp",
                "efaf9a17-2304-4f42-8433-421523c308dc",
                1_000_984_088L
        );

        this.validatePlayerData(
                parsedLeaderboard.get(499),
                "bwear",
                "464872bd-048f-4a17-859f-17b1ce886210",
                13_345_882L
        );

        this.validatePlayerData(
                parsedLeaderboard.get(500),
                "DraZZeLxCaMZZ",
                "0eb0256c-449f-4a13-9d59-4912e5f5f10d",
                13_345_213L
        );
    }
}