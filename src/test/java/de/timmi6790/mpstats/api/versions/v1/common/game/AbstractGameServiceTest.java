package de.timmi6790.mpstats.api.versions.v1.common.game;

import de.timmi6790.mpstats.api.utilities.GameUtilities;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.GameRepository;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.GameCategory;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static de.timmi6790.mpstats.api.utilities.GameUtilities.generateCategoryName;
import static de.timmi6790.mpstats.api.utilities.GameUtilities.generateGameName;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractGameServiceTest {
    private final Supplier<GameService> gameServiceSupplier;
    private final GameRepository gameRepository;
    private final GameService gameService;

    public AbstractGameServiceTest(final Supplier<GameService> gameServiceSupplier) {
        this.gameServiceSupplier = gameServiceSupplier;
        this.gameService = gameServiceSupplier.get();
        this.gameRepository = this.gameService.getGameRepository();
    }

    protected Game generateGame(final String gameName) {
        return GameUtilities.generateGame(this.gameService, gameName);
    }

    protected Game generateGame() {
        return GameUtilities.generateGame(this.gameService);
    }

    @Test
    void hasGame() {
        final String gameName = generateGameName();

        final boolean gameNotFound = this.gameService.hasGame(gameName);
        assertThat(gameNotFound).isFalse();

        this.generateGame(gameName);

        final boolean gameFound = this.gameService.hasGame(gameName);
        assertThat(gameFound).isTrue();
    }

    @Test
    void hasGame_case_insensitive() {
        final String gameName = generateGameName();
        this.generateGame(gameName);

        final boolean gameFoundLower = this.gameService.hasGame(gameName.toLowerCase());
        assertThat(gameFoundLower).isTrue();

        final boolean gameFoundUpper = this.gameService.hasGame(gameName.toUpperCase());
        assertThat(gameFoundUpper).isTrue();
    }

    @Test
    void getGames() {
        final Game game1 = this.generateGame();
        final Game game2 = this.generateGame();

        final List<Game> games = this.gameService.getGames();
        assertThat(games).containsAll(Arrays.asList(game1, game2));
    }

    @Test
    void getGame_case_insensitive() {
        final String gameName = generateGameName();
        this.generateGame(gameName);

        final Optional<Game> gameFoundLower = this.gameService.getGame(gameName.toLowerCase());
        assertThat(gameFoundLower).isPresent();

        final Optional<Game> gameFoundUpper = this.gameService.getGame(gameName.toUpperCase());
        assertThat(gameFoundUpper).isPresent();

        assertThat(gameFoundLower).contains(gameFoundUpper.get());
    }

    @Test
    void createGame() {
        final String websiteName = generateGameName();
        final String cleanName = generateGameName();
        final String gameName = generateGameName();
        final String categoryName = generateCategoryName();

        final Optional<Game> gameNotFound = this.gameService.getGame(gameName);
        assertThat(gameNotFound).isNotPresent();

        final Game createdGame = this.gameService.getOrCreateGame(websiteName, gameName, cleanName, categoryName);
        assertThat(createdGame.websiteName()).isEqualTo(websiteName);
        assertThat(createdGame.cleanName()).isEqualTo(cleanName);
        assertThat(createdGame.gameName()).isEqualTo(gameName);
        assertThat(createdGame.categoryName()).isEqualTo(categoryName);

        final Optional<Game> gameFound = this.gameService.getGame(gameName);
        assertThat(gameFound)
                .isPresent()
                .contains(createdGame);

        final Optional<Game> gameCacheFound = this.gameRepository.getGame(gameName);
        assertThat(gameCacheFound)
                .isPresent()
                .contains(createdGame);
    }

    @Test
    void createGame_duplicate() {
        final String websiteName = generateGameName();
        final String cleanName = generateGameName();
        final String categoryName = generateCategoryName();
        final String gameName = generateGameName();

        final Game game1 = this.gameService.getOrCreateGame(websiteName, gameName, cleanName, categoryName);
        final Game game2 = this.gameService.getOrCreateGame(websiteName, gameName, cleanName, categoryName);

        assertThat(game1).isEqualTo(game2);
    }

    @Test
    void deleteGame() {
        final String gameName = generateGameName();
        this.generateGame(gameName);

        this.gameService.deleteGame(gameName);

        final boolean notFound = this.gameService.hasGame(gameName);
        assertThat(notFound).isFalse();

        final Optional<Game> gameNotFound = this.gameService.getGame(gameName);
        assertThat(gameNotFound).isNotPresent();
    }

    @Test
    void deleteGame_case_insensitive() {
        final String gameName = generateGameName();
        this.generateGame(gameName);

        this.gameService.deleteGame(gameName.toLowerCase());

        final boolean notFound = this.gameService.hasGame(gameName);
        assertThat(notFound).isFalse();

        final Optional<Game> gameNotFound = this.gameService.getGame(gameName);
        assertThat(gameNotFound).isNotPresent();
    }

    @Test
    void innit_with_existing_games() {
        final String gameName = generateGameName();
        final Game game = this.generateGame(gameName);

        final GameService newGameService = this.gameServiceSupplier.get();

        final boolean foundGame = newGameService.hasGame(gameName);
        assertThat(foundGame).isTrue();

        final Optional<Game> gameFound = newGameService.getGame(gameName);
        assertThat(gameFound)
                .isPresent()
                .contains(game);
    }

    @Test
    void innit_with_existing_categories() {
        final String categoryName = generateCategoryName();
        final GameCategory category = this.gameService.getCategoryOrCreate(categoryName);

        final GameService newGameService = this.gameServiceSupplier.get();

        final boolean foundCategory = newGameService.hasCategory(categoryName);
        assertThat(foundCategory).isTrue();

        final Optional<GameCategory> categoryFound = newGameService.getCategory(categoryName);
        assertThat(categoryFound)
                .isPresent()
                .contains(category);
    }

    @Test
    void getOrCreateCategory_duplicate() {
        final String categoryName = generateCategoryName();
        final GameCategory category = this.gameService.getCategoryOrCreate(categoryName);
        final GameCategory categoryDuplicate = this.gameService.getCategoryOrCreate(categoryName);

        assertThat(category).isEqualTo(categoryDuplicate);

        // Check repository
        final Optional<GameCategory> categoryRepository = this.gameRepository.getGameCategory(categoryName);
        assertThat(categoryRepository)
                .isPresent()
                .contains(category);
    }
}