package de.timmi6790.mpstats.api.versions.v1.website.parser;

import de.timmi6790.mpstats.api.versions.v1.website.models.WebsitePlayerModel;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WebsiteParser {
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36";

    private static final String GLOBAL_GAME_NAME = "Global";
    private static final String CLANS_GAME_NAME = "Clans";
    private static final int CLANS_STATS_POSITION = 5;

    private static final Pattern PLAYER_UUID_PATTERN = Pattern.compile("^https:\\/\\/crafatar\\.com\\/renders\\/body\\/(\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12})\\?helm=true$");

    private static final Pattern CARD_BODY_PATTERN = Pattern.compile("^(.*)<span.*\">([\\d,]*)<\\/span>$");
    private static final Pattern CARD_BODY_TIME_PLAYED_PATTERN = Pattern.compile("(.*)<span.*\">([\\d,]*) days<\\/span>");

    private static final Pattern GENERAL_STATS_PATTERN = Pattern.compile("^([\\d.,]*) (.*)$");

    private final WebsiteConverter websiteConverter;
    private final WebsiteFilter websiteFilter;

    @Autowired
    public WebsiteParser(final WebsiteConverter websiteConverter, final WebsiteFilter websiteFilter) {
        this.websiteConverter = websiteConverter;
        this.websiteFilter = websiteFilter;
    }

    private void addToStatsList(final String game,
                                final String stat,
                                final String value,
                                final Map<String, Long> statsMap) {
        final String statValue = this.websiteConverter.convertStat(game, stat);
        // Don't save stats we don't want to show
        if (this.websiteFilter.isStatFiltered(game, statValue)) {
            return;
        }

        statsMap.put(stat, this.websiteConverter.convertValue(game, stat, value));
    }

    private Matcher getClansCardMatcher(final String htmlBody) {
        final Matcher cardBodyMatcher = CARD_BODY_PATTERN.matcher(htmlBody);
        if (cardBodyMatcher.find()) {
            return cardBodyMatcher;
        }

        final Matcher cardBodyTimeMatcher = CARD_BODY_TIME_PLAYED_PATTERN.matcher(htmlBody);
        if (cardBodyTimeMatcher.find()) {
            return cardBodyTimeMatcher;
        }

        return null;
    }

    protected Optional<String> getHtmlString(final String playerName) {
        try {
            final HttpResponse<String> response = Unirest.get("https://www.mineplex.com/players/{player}")
                    .routeParam("player", playerName)
                    .header("User-Agent", USER_AGENT)
                    .connectTimeout(15_000)
                    .asString();

            if (response.isSuccess()) {
                return Optional.of(response.getBody());
            }

        } catch (final Exception ignore) {
        }

        return Optional.empty();
    }

    private Optional<UUID> getPlayerUUID(final Document doc) {
        final Elements imageElements = doc.select("img");
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

    public Optional<WebsitePlayerModel> retrievePlayerStats(final String player) {
        final Optional<String> htmlOpt = this.getHtmlString(player);
        if (htmlOpt.isEmpty()) {
            return Optional.empty();
        }

        final Document doc = Jsoup.parse(htmlOpt.get());

        // Check if "Timmi6790's Stats | Players" exists to see if the name is valid or not
        if (!doc.select("div.titleBar").hasText()) {
            return Optional.empty();
        }

        // The player name has 2 parts the Rank followed by the name. Using the last element after splitting it should always return the name.
        // This will break for names with spaces, but that is really rare
        final String[] playerNameParts = doc.selectFirst("p.www-mp-username").text().split(" ");
        final String playerName = playerNameParts[playerNameParts.length - 1];
        final String rank = playerNameParts.length == 1 ? "Player" : playerNameParts[0];

        // PlayerUUID
        final Optional<UUID> playerUUIDOpt = this.getPlayerUUID(doc);
        if (playerUUIDOpt.isEmpty()) {
            return Optional.empty();
        }

        final Map<String, Map<String, Long>> stats = new LinkedCaseInsensitiveMap<>();

        // Global stats
        final Element globalStatsElement = doc.selectFirst("div.generalStatsSide");
        if (globalStatsElement != null) {
            final Map<String, Long> globalStats = new LinkedCaseInsensitiveMap<>();
            for (final Element element : globalStatsElement.select("p")) {
                final Elements span = element.select("span");
                if (span.size() == 2) {
                    this.addToStatsList(GLOBAL_GAME_NAME, span.get(0).text(), span.get(1).text(), globalStats);
                }
            }
            stats.put(GLOBAL_GAME_NAME, globalStats);
        }

        // Special handling for clans, because it has no "Show more" section
        // There is no good way to detect the clans stats section. I could check for the picture, but that is too slow.
        final Element clansElement = doc.select("div.card-body").get(CLANS_STATS_POSITION);
        if (clansElement != null) {
            final Map<String, Long> clansStats = new LinkedCaseInsensitiveMap<>();
            for (final Element element : clansElement.select("p")) {
                final Matcher clansMatcher = this.getClansCardMatcher(element.html());
                if (clansMatcher != null) {
                    this.addToStatsList(CLANS_GAME_NAME, clansMatcher.group(1), clansMatcher.group(2), clansStats);
                }
            }
            stats.put(CLANS_GAME_NAME, clansStats);
        }

        // All other games
        for (final Element element : doc.select("div.playersFullGameStats")) {
            final Elements gameNameElements = element.select("div.playersGameName");
            final Elements generalStatsElements = element.select("div.playersGameGeneralStats");
            final Elements statsTableElements = element.select("table.playersStatsTable");
            // We can currently assume that each game inside the games have the same amount of elements
            final int tableSplitCount = statsTableElements.size() / gameNameElements.size();

            for (int index = 0; gameNameElements.size() > index; index++) {
                final String gameName = this.websiteConverter.convertGame(gameNameElements.get(index).text());
                final Map<String, Long> statsMap = new LinkedCaseInsensitiveMap<>();

                // General stats
                for (final Element generalElement : generalStatsElements.get(index).select("td")) {
                    final Matcher matcher = GENERAL_STATS_PATTERN.matcher(generalElement.text());
                    if (matcher.find()) {
                        this.addToStatsList(gameName, matcher.group(2), matcher.group(1), statsMap);
                    }
                }

                // Table stats
                final int maxTableSize = Math.min(tableSplitCount * index + tableSplitCount, statsTableElements.size());
                for (int tableIndex = tableSplitCount * index; maxTableSize > tableIndex; tableIndex++) {
                    for (final Element tableElement : statsTableElements.get(tableIndex).select("tr")) {
                        if (tableElement.select("th").hasText() && tableElement.select("td").hasText()) {
                            this.addToStatsList(
                                    gameName,
                                    tableElement.select("th").text(),
                                    tableElement.select("td").text(),
                                    statsMap
                            );
                        }
                    }
                }

                stats.put(gameName, statsMap);
            }
        }

        return Optional.of(
                new WebsitePlayerModel(
                        playerName,
                        playerUUIDOpt.get(),
                        rank,
                        stats
                )
        );
    }
}
