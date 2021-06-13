package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_request;

import com.google.re2j.Pattern;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardSave;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.okhttp3.OkHttpConnectionPoolMetrics;
import io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener;
import io.sentry.Sentry;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Data
@Log4j2
public abstract class LeaderboardRequestService<P extends Player> {
    private static final int RETRY_COUNT = 5;
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36";
    private static final Pattern HTML_ROW_PARSER = Pattern.compile("<tr>|<tr >|<tr class=\"LeaderboardsOdd\">|<tr class=\"LeaderboardsHead\">[^<]*");

    @Getter(AccessLevel.PROTECTED)
    private final String leaderboardBaseUrl;

    @Getter(AccessLevel.PROTECTED)
    private final OkHttpClient httpClient;

    protected LeaderboardRequestService(final String leaderboardBaseUrl, final MeterRegistry meterRegistry) {
        this.leaderboardBaseUrl = leaderboardBaseUrl;

        final Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(100);
        dispatcher.setMaxRequestsPerHost(100);

        final ConnectionPool connectionPool = new ConnectionPool(
                100,
                15000,
                TimeUnit.MILLISECONDS
        );

        new OkHttpConnectionPoolMetrics(connectionPool).bindTo(meterRegistry);
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(45, TimeUnit.SECONDS)
                .dispatcher(dispatcher)
                .connectionPool(connectionPool)
                .eventListener(OkHttpMetricsEventListener.builder(meterRegistry, "okhttp.requests")
                        .uriMapper(req -> req.url().encodedPath())
                        .build())
                .addInterceptor(chain -> {
                    final Request originalRequest = chain.request();
                    final Request requestWithUserAgent = originalRequest.newBuilder()
                            .header("User-Agent", USER_AGENT)
                            .build();
                    return chain.proceed(requestWithUserAgent);
                })
                .addInterceptor(chain -> {
                    final Request originalRequest = chain.request();
                    Response response = chain.proceed(originalRequest);

                    int tryCount = 0;
                    while (!response.isSuccessful() && tryCount < RETRY_COUNT) {
                        tryCount++;
                        response = chain.proceed(originalRequest);
                    }

                    return response;
                })
                .build();
    }

    protected abstract List<LeaderboardEntry<P>> parseRows(String[] rows);

    protected List<LeaderboardEntry<P>> parseLeaderboardEntry(final String response) {
        final String[] rows = HTML_ROW_PARSER.split(response);
        return this.parseRows(rows);
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
                                    ZonedDateTime.now(),
                                    parsedResponse
                            )
                    );
                }

            } else {
                log.info("Empty response for {}-{}-{}", game, stat, board);
            }
            return Optional.empty();
        } catch (final SocketTimeoutException e) {
            log.error("{}-{}-{}", game, stat, board, e);
            return Optional.empty();
        } catch (final IOException e) {
            log.error("{}-{}-{}", game, stat, board, e);
            Sentry.captureException(e);
            return Optional.empty();
        }
    }
}
