package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_request;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_request.models.WebLeaderboard;
import kong.unirest.HttpMethod;
import kong.unirest.MockClient;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractLeaderboardRequestTest<L extends WebLeaderboard> {
    private final AbstractLeaderboardRequest<L> leaderboardRequest;

    public AbstractLeaderboardRequestTest(final AbstractLeaderboardRequest<L> leaderboardRequest) {
        this.leaderboardRequest = leaderboardRequest;
    }

    @SneakyThrows
    private String getContentFromFile(final String path) {
        final ClassLoader classLoader = AbstractLeaderboardRequestTest.class.getClassLoader();

        final URI uri = classLoader.getResource(path).toURI();
        final byte[] encoded = Files.readAllBytes(Paths.get(uri));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    private MockClient getMockClient() {
        return MockClient.register(this.leaderboardRequest.getUnirest());
    }

    protected Optional<List<L>> retrieveLeaderboard(final String responsePath) {
        final String content = this.getContentFromFile(responsePath);
        final MockClient mock = this.getMockClient();

        mock.expect(HttpMethod.GET, this.leaderboardRequest.getLeaderboardBaseUrl())
                .thenReturn(content);

        final Optional<List<L>> leaderboard = this.leaderboardRequest.retrieveLeaderboard("", "", "");

        mock.verifyAll();
        return leaderboard;
    }

    @Test
    void emptyResponse() {
        final MockClient mock = this.getMockClient();
        
        mock.expect(HttpMethod.GET, this.leaderboardRequest.getLeaderboardBaseUrl())
                .thenReturn("");

        final Optional<List<L>> leaderboard = this.leaderboardRequest.retrieveLeaderboard("", "", "");

        assertThat(leaderboard).isNotPresent();
    }
}