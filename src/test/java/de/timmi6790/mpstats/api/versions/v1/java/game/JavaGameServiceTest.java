package de.timmi6790.mpstats.api.versions.v1.java.game;

import de.timmi6790.mpstats.api.AbstractIntegrationTest;
import de.timmi6790.mpstats.api.versions.v1.java.game.repository.JavaGameRepository;
import de.timmi6790.mpstats.api.versions.v1.java.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.java.game.repository.models.GameCategory;
import de.timmi6790.mpstats.api.versions.v1.java.game.repository.postgres.JavaGamePostgresRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;


class JavaGameServiceTest {
    private static JavaGameRepository javaGameRepository;
    private static JavaGameService javaGameService;

    private static final AtomicInteger GAME_ID = new AtomicInteger(0);
    private static final AtomicInteger CATEGORY_ID = new AtomicInteger(0);

    @BeforeAll
    static void setUp() {
        javaGameRepository = new JavaGamePostgresRepository(AbstractIntegrationTest.jdbi());
        javaGameService = new JavaGameService(javaGameRepository);
    }

    private String generateGameName() {
        return "GAME" + GAME_ID.incrementAndGet();
    }

    private String generateCategoryName() {
        return "CATEGORY" + CATEGORY_ID.incrementAndGet();
    }

    private Game generateGame(final String gameName) {
        final String websiteName = this.generateGameName();
        final String cleanName = this.generateGameName();
        final String categoryName = this.generateCategoryName();

        return javaGameService.getOrCreateGame(websiteName, gameName, cleanName, categoryName);
    }

    @Test
    void hasGame() {
        final String gameName = this.generateGameName();

        final boolean gameNotFound = javaGameService.hasGame(gameName);
        assertThat(gameNotFound).isFalse();

        this.generateGame(gameName);

        final boolean gameFound = javaGameService.hasGame(gameName);
        assertThat(gameFound).isTrue();
    }

    @Test
    void hasGame_case_insensitive() {
        final String gameName = this.generateGameName();
        this.generateGame(gameName);

        final boolean gameFoundLower = javaGameService.hasGame(gameName.toLowerCase());
        assertThat(gameFoundLower).isTrue();

        final boolean gameFoundUpper = javaGameService.hasGame(gameName.toUpperCase());
        assertThat(gameFoundUpper).isTrue();
    }

    @Test
    void getGames() {
        final Game game1 = this.generateGame(this.generateGameName());
        final Game game2 = this.generateGame(this.generateGameName());

        final List<Game> games = javaGameService.getGames();
        assertThat(games).containsAll(Arrays.asList(game1, game2));
    }

    @Test
    void getGame_case_insensitive() {
        final String gameName = this.generateGameName();
        this.generateGame(gameName);

        final Optional<Game> gameFoundLower = javaGameService.getGame(gameName.toLowerCase());
        assertThat(gameFoundLower).isPresent();

        final Optional<Game> gameFoundUpper = javaGameService.getGame(gameName.toUpperCase());
        assertThat(gameFoundUpper).isPresent();

        assertThat(gameFoundLower).contains(gameFoundUpper.get());
    }

    @Test
    void createGame() {
        final String websiteName = this.generateGameName();
        final String cleanName = this.generateGameName();
        final String gameName = this.generateGameName();
        final String categoryName = this.generateCategoryName();

        final Optional<Game> gameNotFound = javaGameService.getGame(gameName);
        assertThat(gameNotFound).isNotPresent();

        final Game createdGame = javaGameService.getOrCreateGame(websiteName, gameName, cleanName, categoryName);
        assertThat(createdGame.getWebsiteName()).isEqualTo(websiteName);
        assertThat(createdGame.getCleanName()).isEqualTo(cleanName);
        assertThat(createdGame.getGameName()).isEqualTo(gameName);
        assertThat(createdGame.getCategoryName()).isEqualTo(categoryName);

        final Optional<Game> gameFound = javaGameService.getGame(gameName);
        assertThat(gameFound).isPresent();
        assertThat(gameFound).contains(createdGame);

        final Optional<Game> gameCacheFound = javaGameRepository.getGame(gameName);
        assertThat(gameCacheFound).isPresent();
        assertThat(gameCacheFound).contains(createdGame);
    }

    @Test
    void createGame_duplicate() {
        final String websiteName = this.generateGameName();
        final String cleanName = this.generateGameName();
        final String categoryName = this.generateCategoryName();
        final String gameName = this.generateGameName();

        final Game game1 = javaGameService.getOrCreateGame(websiteName, gameName, cleanName, categoryName);
        final Game game2 = javaGameService.getOrCreateGame(websiteName, gameName, cleanName, categoryName);

        assertThat(game1).isEqualTo(game2);
    }

    @Test
    void deleteGame() {
        final String gameName = this.generateGameName();
        this.generateGame(gameName);

        javaGameService.deleteGame(gameName);

        final boolean notFound = javaGameService.hasGame(gameName);
        assertThat(notFound).isFalse();

        final Optional<Game> gameNotFound = javaGameService.getGame(gameName);
        assertThat(gameNotFound).isNotPresent();
    }

    @Test
    void deleteGame_case_insensitive() {
        final String gameName = this.generateGameName();
        this.generateGame(gameName);

        javaGameService.deleteGame(gameName.toLowerCase());

        final boolean notFound = javaGameService.hasGame(gameName);
        assertThat(notFound).isFalse();

        final Optional<Game> gameNotFound = javaGameService.getGame(gameName);
        assertThat(gameNotFound).isNotPresent();
    }

    @Test
    void innit_with_existing_games() {
        final String gameName = this.generateGameName();
        final Game game = this.generateGame(gameName);

        final JavaGameService newJavaGameService = new JavaGameService(javaGameRepository);

        final boolean foundGame = newJavaGameService.hasGame(gameName);
        assertThat(foundGame).isTrue();

        final Optional<Game> gameFound = newJavaGameService.getGame(gameName);
        assertThat(gameFound).isPresent();
        assertThat(gameFound).contains(game);
    }

    @Test
    void innit_with_existing_categories() {
        final String categoryName = this.generateCategoryName();
        final GameCategory category = javaGameService.getCategoryOrCreate(categoryName);

        final JavaGameService newJavaGameService = new JavaGameService(javaGameRepository);

        final boolean foundCategory = newJavaGameService.hasCategory(categoryName);
        assertThat(foundCategory).isTrue();

        final Optional<GameCategory> categoryFound = newJavaGameService.getCategory(categoryName);
        assertThat(categoryFound).isPresent();
        assertThat(categoryFound).contains(category);
    }

    @Test
    void getOrCreateCategory_duplicate() {
        final String categoryName = this.generateCategoryName();
        final GameCategory category = javaGameService.getCategoryOrCreate(categoryName);
        final GameCategory categoryDuplicate = javaGameService.getCategoryOrCreate(categoryName);

        assertThat(category).isEqualTo(categoryDuplicate);
    }
}