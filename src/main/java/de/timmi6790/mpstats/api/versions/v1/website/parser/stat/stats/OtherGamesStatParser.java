package de.timmi6790.mpstats.api.versions.v1.website.parser.stat.stats;

import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import de.timmi6790.mpstats.api.versions.v1.website.parser.stat.BaseStatParser;
import de.timmi6790.mpstats.api.versions.v1.website.parser.stat.ParserResult;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OtherGamesStatParser extends BaseStatParser {
    private static final Pattern GENERAL_STATS_PATTERN = Pattern.compile("^([\\d.,]*) (.*)$");

    public OtherGamesStatParser(final GameService gameService, final StatService statService) {
        super(gameService, statService);
    }

    @Override
    public ParserResult parse(final Document document) {
        final ParserResult parserResult = new ParserResult();

        for (final Element element : document.select("div.playersFullGameStats")) {
            final Elements gameNameElements = element.select("div.playersGameName");
            final Elements generalStatsElements = element.select("div.playersGameGeneralStats");
            final Elements statsTableElements = element.select("table.playersStatsTable");
            // We can currently assume that each game inside the games have the same amount of elements
            final int tableSplitCount = statsTableElements.size() / gameNameElements.size();

            for (int index = 0; gameNameElements.size() > index; index++) {
                final Optional<Game> gameOpt = this.getGame(gameNameElements.get(index).text());
                if (gameOpt.isEmpty()) {
                    continue;
                }

                final Game game = gameOpt.get();

                // General stats
                for (final Element generalElement : generalStatsElements.get(index).select("td")) {
                    final Matcher matcher = GENERAL_STATS_PATTERN.matcher(generalElement.text());
                    if (matcher.find()) {
                        final String cleanStatName = this.convertStatName(matcher.group(2));
                        final String valueText = matcher.group(1);
                        this.addStat(parserResult, game, cleanStatName, valueText);
                    }
                }

                // Table stats
                final int maxTableSize = Math.min(tableSplitCount * index + tableSplitCount, statsTableElements.size());
                for (int tableIndex = tableSplitCount * index; maxTableSize > tableIndex; tableIndex++) {
                    for (final Element tableElement : statsTableElements.get(tableIndex).select("tr")) {
                        final String statName = tableElement.select("th").text();
                        final String valueText = tableElement.select("td").text();

                        if (!statName.isEmpty() && !valueText.isEmpty()) {
                            final String cleanStatName = this.convertStatName(statName);
                            this.addStat(parserResult, game, cleanStatName, valueText);
                        }
                    }
                }
            }
        }

        return parserResult;
    }
}
