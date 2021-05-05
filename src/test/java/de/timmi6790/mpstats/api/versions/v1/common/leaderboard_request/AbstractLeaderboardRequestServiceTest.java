package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_request;

import de.timmi6790.mpstats.api.Config;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardSave;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.SneakyThrows;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractLeaderboardRequestServiceTest<P extends Player, S extends PlayerService<P>> {
    private final BiFunction<Config, S, LeaderboardRequestService<P>> leaderboardRequestFunction;
    private final S playerService;

    protected AbstractLeaderboardRequestServiceTest(final BiFunction<Config, S, LeaderboardRequestService<P>> leaderboardRequestFunction,
                                                    final S playerService) {
        this.leaderboardRequestFunction = leaderboardRequestFunction;
        this.playerService = playerService;
    }

    @SneakyThrows
    protected String getContentFromFile(final String path) {
        final ClassLoader classLoader = AbstractLeaderboardRequestServiceTest.class.getClassLoader();

        final URI uri = classLoader.getResource(path).toURI();
        final byte[] encoded = Files.readAllBytes(Paths.get(uri));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    protected LeaderboardRequestService<P> getLeaderboardRequestService(final String url) {
        final Config config = new Config();
        final Config.MineplexLeaderboardConfig leaderboardConfig = config.getLeaderboard();
        leaderboardConfig.setBedrockUrl(url);
        leaderboardConfig.setJavaUrl(url);

        return this.leaderboardRequestFunction.apply(config, this.playerService);
    }

    protected Optional<List<LeaderboardEntry<P>>> retrieveLeaderboard(final String responsePath) {
        final String content = this.getContentFromFile(responsePath);

        try (final MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse().setBody(content));

            final HttpUrl url = server.url("");
            final LeaderboardRequestService<P> leaderboardRequest = this.getLeaderboardRequestService(url.toString());

            final Optional<LeaderboardSave<P>> leaderboard = leaderboardRequest.retrieveLeaderboard(
                    "",
                    "",
                    ""
            );

            return leaderboard.map(LeaderboardSave::getEntries);
        } catch (final IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Test
    void emptyResponse() {
        try (final MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse().setBody(""));

            final HttpUrl url = server.url("");
            final LeaderboardRequestService<P> leaderboardRequest = this.getLeaderboardRequestService(url.toString());

            final Optional<LeaderboardSave<P>> leaderboard = leaderboardRequest.retrieveLeaderboard(
                    "",
                    "",
                    ""
            );

            assertThat(leaderboard).isNotPresent();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}