package de.timmi6790.mpstats.api.versions.v1.common.player_stats.generator.generators.cakewars;

import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.generator.GameBoardStatGenerator;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.generator.StatGeneratorData;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.models.GeneratedPlayerEntry;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.models.PlayerEntry;

import java.util.*;

public class CakeBitesPerGameGenerator extends GameBoardStatGenerator {
    private static final Set<String> ALLOWED_GAMES_WEBSITE_NAMES = Set.of("Cake%20Wars%20Standard", "Cake%20Wars%20Duos");
    private static final String CAKE_BITES_WEBSITE_STAT_NAME = "Bites";

    @Override
    protected Collection<GeneratedPlayerEntry> generateGameBoardStats(final StatGeneratorData generatorData,
                                                                      final Game game,
                                                                      final Board board,
                                                                      final Map<String, PlayerEntry> playerEntries) {
        if (!ALLOWED_GAMES_WEBSITE_NAMES.contains(game.getWebsiteName())) {
            return Collections.emptyList();
        }

        final PlayerEntry bitesEntry = playerEntries.get(CAKE_BITES_WEBSITE_STAT_NAME);
        if (!this.isPresent(bitesEntry)) {
            return Collections.emptyList();
        }

        return this.getGamesPlayed(playerEntries).map(gamesPlayed ->
                List.of(
                        new GeneratedPlayerEntry(
                                "CakeBitesPerGame",
                                game,
                                board,
                                (double) bitesEntry.getScore() / gamesPlayed
                        )
                )
        ).orElseGet(Collections::emptyList);
    }
}
