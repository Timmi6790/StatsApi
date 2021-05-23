package de.timmi6790.mpstats.api.versions.v1.website.parser;

import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.java.game.JavaGameService;
import de.timmi6790.mpstats.api.versions.v1.java.stat.JavaStatService;
import de.timmi6790.mpstats.api.versions.v1.website.models.GameStat;
import de.timmi6790.mpstats.api.versions.v1.website.models.WebsitePlayer;
import de.timmi6790.mpstats.api.versions.v1.website.parser.stat.ParserResult;
import de.timmi6790.mpstats.api.versions.v1.website.parser.stat.StatParser;
import de.timmi6790.mpstats.api.versions.v1.website.parser.stat.stats.ClansStatsParser;
import de.timmi6790.mpstats.api.versions.v1.website.parser.stat.stats.GlobalStatsParser;
import de.timmi6790.mpstats.api.versions.v1.website.parser.stat.stats.OtherGamesStatParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WebsiteParser {
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36";

    private static final Pattern PLAYER_UUID_PATTERN = Pattern.compile("^https:\\/\\/crafatar\\.com\\/renders\\/body\\/(\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12})\\?helm=true$");

    private final OkHttpClient httpClient;

    private final List<StatParser> statParsers;

    @Autowired
    public WebsiteParser(final JavaGameService gameService,
                         final JavaStatService statService) {
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

        this.statParsers = List.of(
                new GlobalStatsParser(gameService, statService),
                new ClansStatsParser(gameService, statService),
                new OtherGamesStatParser(gameService, statService)
        );
    }

    private Optional<UUID> getPlayerUUID(final Document document) {
        final Elements imageElements = document.select("img");
        for (final Element imageElement : imageElements) {
            final String srcValue = imageElement.attr("src");
            final Matcher matcher = PLAYER_UUID_PATTERN.matcher(srcValue);
            if (matcher.find()) {
                final UUID playerUUID = UUID.fromString(matcher.group(1));
                return Optional.of(playerUUID);
            }
        }
        return Optional.empty();
    }

    protected Request constructRequest(final String url) {
        return new Request.Builder()
                .url(url)
                .build();
    }

    protected Optional<Document> getDocument(final String playerName) {
        final Request request = this.constructRequest("https://www.mineplex.com/players/" + playerName);
        try (final Response response = this.httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                try (final InputStream inputStream = response.body().byteStream()) {
                    return Optional.ofNullable(
                            Jsoup.parse(inputStream, null, "https://www.mineplex.com")
                    );
                }
            }
            return Optional.empty();
        } catch (final IOException e) {
            return Optional.empty();
        }
    }

    public Optional<WebsitePlayer> retrievePlayerStats(final String player) {
        final Optional<Document> documentOpt = this.getDocument(player);
        if (documentOpt.isEmpty()) {
            return Optional.empty();
        }

        final Document document = documentOpt.get();

        // Check if "Timmi6790's Stats | Players" exists to see if the name is valid or not
        if (!document.select("div.titleBar").hasText()) {
            return Optional.empty();
        }

        // The player name has 2 parts the Rank followed by the name. Using the last element after splitting it should always return the name.
        // This will break for names with spaces, but that is really rare
        final String[] playerNameParts = document.selectFirst("p.www-mp-username").text().split(" ");
        final String playerName = playerNameParts[playerNameParts.length - 1];
        final String rank = playerNameParts.length == 1 ? "Player" : playerNameParts[0];

        // PlayerUUID
        final Optional<UUID> playerUUIDOpt = this.getPlayerUUID(document);
        if (playerUUIDOpt.isEmpty()) {
            return Optional.empty();
        }

        final Map<Game, GameStat> stats = new HashMap<>();
        for (final StatParser statParser : this.statParsers) {
            final ParserResult parserResult = statParser.parse(document);
            for (final Map.Entry<Game, GameStat> entry : parserResult.getStats().entrySet()) {
                final Game game = entry.getKey();
                final GameStat gameStat = stats.get(game);
                if (gameStat == null) {
                    stats.put(game, entry.getValue());
                } else {
                    gameStat.merge(entry.getValue());
                }
            }
        }

        return Optional.of(
                new WebsitePlayer(
                        playerName,
                        playerUUIDOpt.get(),
                        rank,
                        stats
                )
        );
    }
}
