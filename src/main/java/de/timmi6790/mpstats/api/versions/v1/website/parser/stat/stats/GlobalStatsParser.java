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

public class GlobalStatsParser extends BaseStatParser {
    private static final String GLOBAL_GAME_NAME = "Global";

    public GlobalStatsParser(final GameService gameService, final StatService statService) {
        super(gameService, statService);
    }

    @Override
    public ParserResult parse(final Document document) {
        final ParserResult parserResult = new ParserResult();

        final Element globalStatsElement = document.selectFirst("div.generalStatsSide");
        if (globalStatsElement != null) {
            final Optional<Game> gameOpt = this.getGame(GLOBAL_GAME_NAME);
            if (gameOpt.isEmpty()) {
                return parserResult;
            }

            final Game game = gameOpt.get();
            for (final Element element : globalStatsElement.select("p")) {
                final Elements span = element.select("span");
                if (span.size() == 2) {
                    final String cleanStatName = this.convertStatName(span.get(0).text());
                    final String valueText = span.get(1).text();
                    this.addStat(parserResult, game, cleanStatName, valueText);
                }
            }
        }

        return parserResult;
    }
}
