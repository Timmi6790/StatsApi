package de.timmi6790.mineplex_stats_api.versions.v1.website.service;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.timmi6790.mineplex_stats_api.versions.v1.models.StatModel;
import de.timmi6790.mineplex_stats_api.versions.v1.website.model.WebsitePlayerModel;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WebsiteService {
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36";

    private static final String GLOBAL_GAME_NAME = "Global";
    private static final String CLANS_GAME_NAME = "Clans";
    private static final int CLANS_STATS_POSITION = 5;

    private static final Pattern PLAYER_UUID_PATTERN = Pattern.compile("^https:\\/\\/crafatar\\.com\\/renders\\/body\\/(\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12})\\?helm=true$");

    private static final Pattern CARD_BODY_PATTERN = Pattern.compile("^(.*)<span.*\">([\\d,]*)<\\/span>$");
    private static final Pattern CARD_BODY_TIME_PLAYED_PATTERN = Pattern.compile("(.*)<span.*\">([\\d,]*) days<\\/span>");

    private static final Pattern GENERAL_STATS_PATTERN = Pattern.compile("^([\\d.,]*) (.*)$");

    private static final Set<String> GENERAL_FILTERED_STATS = new HashSet<>(Arrays.asList(
            "KDR",
            "Total games",
            "Damage Taken in PvP",
            "Damage Taken",
            "Damage Dealt",
            "Blue Kills",
            "Yellow Kills",
            "Red Kills",
            "Green Kills",
            "Blue Deaths",
            "Yellow Deaths",
            "Red Deaths",
            "Green Deaths"
    ));

    private final AsyncLoadingCache<String, Optional<WebsitePlayerModel>> playerStatsCache = Caffeine
            .newBuilder()
            .buildAsync(this::retrievePlayerStats);

    public static void main(final String[] args) {
        final WebsiteService websiteService = new WebsiteService();
        System.out.println(websiteService.retrievePlayerStats("HoneyNutChris"));
    }

    private long convertValue(final String game, final String stat, final String value) {
        if (game.equalsIgnoreCase(CLANS_GAME_NAME) && stat.equalsIgnoreCase("Time Played")) {
            return TimeUnit.DAYS.toSeconds(Long.parseLong(value.replace(",", "")));
        }

        return Long.parseLong(value.replace(",", ""));
    }

    private String convertGame(final String game) {
        return game
                .replace(" ", "")
                .replace("Solo", "");
    }

    private String convertStat(final String game, final String stat) {
        return stat;
    }

    private boolean isStatFiltered(final String game, final String stat) {
        if (GENERAL_FILTERED_STATS.contains(stat)) {
            return true;
        }

        return false;
    }

    private void addToStatsList(final String game, final String stat, final String value, final List<StatModel> statsMap) {
        final String statValue = this.convertStat(game, stat);
        // Don't save stats we don't want to show
        if (this.isStatFiltered(game, statValue)) {
            return;
        }

        statsMap.add(new StatModel(stat, this.convertValue(game, stat, value)));
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

    public CompletableFuture<Optional<WebsitePlayerModel>> getPlayerStats(final String player) {
        return this.playerStatsCache.get(player);
    }

    private Optional<WebsitePlayerModel> retrievePlayerStats(final String player) {
        final HttpResponse<String> response = Unirest.get("https://www.mineplex.com/players/{player}")
                .routeParam("player", player)
                .header("User-Agent", USER_AGENT)
                .connectTimeout(15_000)
                .asString();

        final Document doc = Jsoup.parse(response.getBody());

        // Check if "Timmi6790's Stats | Players" exists to see if the name is valid or not
        final boolean validPage = doc.select("div.titleBar").hasText();
        if (!validPage) {
            return Optional.empty();
        }

        final Element playerNameElement = doc.selectFirst("p.www-mp-username");

        // The player name has 2 parts the Rank followed by the name. Using the last element after splitting it should always return the name.
        // This will break for names with spaces, but that is really rare
        final String[] playerNameParts = playerNameElement.text().split(" ");
        final String playerName = playerNameParts[playerNameParts.length - 1];
        final String rank = playerNameParts.length == 1 ? "Player" : playerNameParts[0];

        // UUID | I just assume that we will always find one
        UUID playerUUID = null;
        final Elements imageElements = doc.select("img");
        for (final Element imageElement : imageElements) {
            final String srcValue = imageElement.attr("src");
            final Matcher matcher = PLAYER_UUID_PATTERN.matcher(srcValue);
            if (matcher.find()) {
                playerUUID = UUID.fromString(matcher.group(1));
                break;
            }
        }

        final Map<String, List<StatModel>> stats = new TreeMap<>();
        // Global stats
        final Element globalStatsElement = doc.selectFirst("div.generalStatsSide");
        if (globalStatsElement != null) {
            final List<StatModel> globalStats = new ArrayList<>();
            globalStatsElement.select("p")
                    .stream()
                    .map(statData -> statData.select("span"))
                    .filter(elements -> elements.size() == 2)
                    .forEach(elements -> this.addToStatsList(GLOBAL_GAME_NAME, elements.get(0).text(), elements.get(1).text(), globalStats));
            stats.put(GLOBAL_GAME_NAME, globalStats);
        }

        // Special handling for clans, because it has no "Show more" section
        // There is no good way to detect the clans stats section. I could check for the picture, but that is too slow.
        final Element clansElement = doc.select("div.card-body").get(CLANS_STATS_POSITION);
        if (clansElement != null) {
            final List<StatModel> clansStats = new ArrayList<>();
            clansElement.select("p")
                    .stream()
                    .map(Element::html)
                    .map(this::getClansCardMatcher)
                    .filter(Objects::nonNull)
                    .forEach(matcher -> this.addToStatsList(CLANS_GAME_NAME, matcher.group(1), matcher.group(2), clansStats));
            stats.put(CLANS_GAME_NAME, clansStats);
        }

        // All other games
        final Elements gamesElement = doc.select("div.playersFullGameStats");
        gamesElement.forEach(element -> {
            final Elements gameNameElements = element.select("div.playersGameName");
            final Elements generalStatsElements = element.select("div.playersGameGeneralStats");
            final Elements statsTableElements = element.select("table.playersStatsTable");
            // We can currently assume that each game inside the games have the same amount of elements
            final int tableSplitCount = statsTableElements.size() / gameNameElements.size();

            for (int index = 0; gameNameElements.size() > index; index++) {
                final String gameName = this.convertGame(gameNameElements.get(index).text());
                final List<StatModel> statsMap = new ArrayList<>();

                // General stats
                generalStatsElements.get(index)
                        .select("td")
                        .stream()
                        .map(Element::text)
                        .map(GENERAL_STATS_PATTERN::matcher)
                        .filter(Matcher::find)
                        .forEach(matcher -> this.addToStatsList(gameName, matcher.group(2), matcher.group(1), statsMap));

                // Table stats
                final int maxTableSize = Math.min(tableSplitCount * index + tableSplitCount, statsTableElements.size());
                for (int tableIndex = tableSplitCount * index; maxTableSize > tableIndex; tableIndex++) {
                    statsTableElements.get(tableIndex)
                            .select("tr")
                            .stream()
                            .filter(tableElement -> tableElement.select("th").hasText())
                            .filter(tableElement -> tableElement.select("td").hasText())
                            .forEach(tableElement -> this.addToStatsList(gameName, tableElement.select("th").text(), tableElement.select("td").text(), statsMap));
                }

                stats.put(gameName, statsMap);
            }
        });

        return Optional.of(
                new WebsitePlayerModel(
                        playerName,
                        playerUUID,
                        rank,
                        stats
                )
        );
    }
}
