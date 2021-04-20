package de.timmi6790.mpstats.api.utilities;

import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;

import java.util.concurrent.atomic.AtomicInteger;

public class GameUtilities {
    private static final AtomicInteger GAME_ID = new AtomicInteger(0);
    private static final AtomicInteger CATEGORY_ID = new AtomicInteger(0);

    public static String generateGameName() {
        return "Game" + GAME_ID.incrementAndGet();
    }

    public static String generateCategoryName() {
        return "Category" + CATEGORY_ID.incrementAndGet();
    }

    public static Game generateGame(final GameService gameService) {
        final String gameName = generateGameName();

        return generateGame(gameService, gameName);
    }

    public static Game generateGame(final GameService gameService, final String gameName) {
        final String websiteName = generateGameName();
        final String cleanName = generateGameName();
        final String categoryName = generateCategoryName();

        return gameService.getOrCreateGame(websiteName, gameName, cleanName, categoryName);
    }
}
