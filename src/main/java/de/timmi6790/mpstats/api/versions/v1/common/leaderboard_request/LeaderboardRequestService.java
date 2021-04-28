package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_request;

import com.google.common.collect.Lists;
import com.google.re2j.Pattern;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardSave;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Data
@Log4j2
public abstract class LeaderboardRequestService<P extends Player> {
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36";
    private static final Pattern HTML_ROW_PARSER = Pattern.compile("<tr>|<tr >|<tr class=\"LeaderboardsOdd\">|<tr class=\"LeaderboardsHead\">[^<]*");

    @Getter(AccessLevel.PROTECTED)
    private final String leaderboardBaseUrl;
    private final int estimatedResultSize;

    @Getter(AccessLevel.PROTECTED)
    private final OkHttpClient httpClient;

    protected LeaderboardRequestService(final String leaderboardBaseUrl, final int estimatedResultSize) {
        this.leaderboardBaseUrl = leaderboardBaseUrl;
        this.estimatedResultSize = estimatedResultSize;

        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    final Request originalRequest = chain.request();
                    final Request requestWithUserAgent = originalRequest.newBuilder()
                            .header("User-Agent", USER_AGENT)
                            .build();
                    return chain.proceed(requestWithUserAgent);
                })
                .build();
    }

    protected abstract Optional<LeaderboardEntry<P>> parseRow(String row);

    protected List<LeaderboardEntry<P>> parseLeaderboardEntry(final String response) {
        final List<LeaderboardEntry<P>> leaderboard = Lists.newArrayListWithExpectedSize(this.estimatedResultSize);

        final String[] rows = HTML_ROW_PARSER.split(response);
        for (final String row : rows) {
            this.parseRow(row).ifPresent(leaderboard::add);
        }

        return leaderboard;
    }

    public Optional<LeaderboardSave<P>> retrieveLeaderboard(final String game, final String stat, final String board) {
        final HttpUrl url = HttpUrl.parse(this.leaderboardBaseUrl)
                .newBuilder()
                .addQueryParameter("game", game)
                .addQueryParameter("type", stat)
                .addQueryParameter("boardType", board)
                .addQueryParameter("antiCache", String.valueOf(System.currentTimeMillis()))
                .build();

        final Request request = new Request.Builder()
                .url(url)
                .build();

        try (final Response response = this.httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                final String body = response.body().string();

                final List<LeaderboardEntry<P>> parsedResponse = this.parseLeaderboardEntry(body);
                if (!parsedResponse.isEmpty()) {
                    return Optional.of(
                            new LeaderboardSave<>(
                                    LocalDateTime.now(),
                                    parsedResponse
                            )
                    );
                }

            } else {
                log.info("Empty response for {}-{}-{}", game, stat, board);
            }
            return Optional.empty();
        } catch (final IOException e) {
            log.error("{}-{}-{}", game, stat, board, e);
            return Optional.empty();
        }
    }

    public void retrieveLeaderboards(final List<Leaderboard> leaderboards) {
        for (final Leaderboard leaderboard : leaderboards) {
            this.retrieveLeaderboard(
                    leaderboard.game().websiteName(),
                    leaderboard.stat().websiteName(),
                    leaderboard.board().websiteName()
            );
        }
    }
}
