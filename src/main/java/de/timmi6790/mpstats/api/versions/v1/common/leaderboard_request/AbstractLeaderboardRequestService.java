package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_request;

import com.google.common.collect.Lists;
import com.google.re2j.Pattern;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_request.models.WebLeaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Data
@Log4j2
public abstract class AbstractLeaderboardRequestService<PLAYER extends Player> {
    private static final int TIMEOUT = (int) TimeUnit.SECONDS.toMillis(15);
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36";
    private static final Pattern HTML_ROW_PARSER = Pattern.compile("<tr>|<tr >|<tr class=\"LeaderboardsOdd\">|<tr class=\"LeaderboardsHead\">[^<]*");

    @Getter(value = AccessLevel.PROTECTED)
    private final String leaderboardBaseUrl;
    private final int estimatedResultSize;

    @Getter(value = AccessLevel.PROTECTED)
    private final UnirestInstance unirest;

    protected AbstractLeaderboardRequestService(final String leaderboardBaseUrl, final int estimatedResultSize) {
        this.leaderboardBaseUrl = leaderboardBaseUrl;
        this.estimatedResultSize = estimatedResultSize;

        this.unirest = Unirest.spawnInstance();
        this.unirest.config()
                .setDefaultHeader("User-Agent", USER_AGENT)
                .connectTimeout(TIMEOUT);
    }

    protected abstract Optional<WebLeaderboard<PLAYER>> parseRow(String row);

    protected List<WebLeaderboard<PLAYER>> parseWebLeaderboard(final String response) {
        final List<WebLeaderboard<PLAYER>> leaderboard = Lists.newArrayListWithExpectedSize(this.estimatedResultSize);

        final String[] rows = HTML_ROW_PARSER.split(response);
        for (final String row : rows) {
            this.parseRow(row).ifPresent(leaderboard::add);
        }

        return leaderboard;
    }

    public Optional<List<WebLeaderboard<PLAYER>>> retrieveLeaderboard(final String game, final String stat, final String board) {
        final HttpResponse<String> response;
        try {
            response = this.unirest.get(this.leaderboardBaseUrl)
                    .queryString("game", game)
                    .queryString("type", stat)
                    .queryString("boardType", board)
                    .queryString("antiCache", System.currentTimeMillis())
                    .asString();
        } catch (final Exception e) {
            log.error("{}-{}-{}", game, stat, board, e);
            return Optional.empty();
        }

        if (!response.isSuccess() || response.getBody().isEmpty()) {
            log.info("Empty response for {}-{}-{}", game, stat, board);
            return Optional.empty();
        }

        final List<WebLeaderboard<PLAYER>> parsedResponse = this.parseWebLeaderboard(response.getBody());
        if (!parsedResponse.isEmpty()) {
            return Optional.of(parsedResponse);
        }

        return Optional.empty();
    }
}
