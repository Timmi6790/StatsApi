package de.timmi6790.mpstats.api.versions.v1.website.parser.stat.stats;

import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import de.timmi6790.mpstats.api.versions.v1.website.parser.stat.BaseStatParser;
import de.timmi6790.mpstats.api.versions.v1.website.parser.stat.ParserResult;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClansStatsParser extends BaseStatParser {
    private static final String CLANS_GAME_NAME = "Clans";
    private static final int CLANS_STATS_POSITION = 5;

    private static final Pattern CARD_BODY_PATTERN = Pattern.compile("^(.*)<span.*\">([\\d,]*)<\\/span>$");
    private static final Pattern CARD_BODY_TIME_PLAYED_PATTERN = Pattern.compile("(.*)<span.*\">([\\d,]*) days<\\/span>");

    public ClansStatsParser(final GameService gameService, final StatService statService) {
        super(gameService, statService);
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

    @Override
    public ParserResult parse(final Document document) {
        final ParserResult parserResult = new ParserResult();

        // Special handling for clans, because it has no "Show more" section
        // There is no good way to detect the clans stats section. I could check for the picture, but that is too slow.
        final Element clansElement = document.select("div.card-body").get(CLANS_STATS_POSITION);
        if (clansElement != null) {
            final Optional<Game> gameOpt = this.getGame(CLANS_GAME_NAME);
            if (gameOpt.isEmpty()) {
                return parserResult;
            }

            final Game game = gameOpt.get();
            for (final Element element : clansElement.select("p")) {
                final Matcher clansMatcher = this.getClansCardMatcher(element.html());
                if (clansMatcher != null) {
                    final String cleanStatName = this.convertStatName(clansMatcher.group(1));
                    String valueText = clansMatcher.group(2);

                    if ("TimePlaying".equals(cleanStatName)) {
                        final long seconds = TimeUnit.DAYS.toSeconds(this.parseValue(valueText));
                        valueText = String.valueOf(seconds);
                    }

                    this.addStat(parserResult, game, cleanStatName, valueText);
                }
            }
        }

        return parserResult;
    }
}
