package de.timmi6790.mpstats.api.versions.v1.common.game;

import com.google.gson.reflect.TypeToken;
import de.timmi6790.mpstats.api.AbstractRestTest;
import de.timmi6790.mpstats.api.utilities.GameUtilities;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.GameCategory;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static de.timmi6790.mpstats.api.utilities.GameUtilities.generateCategoryName;
import static de.timmi6790.mpstats.api.utilities.GameUtilities.generateGameName;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractGameControllerTest<T extends GameService> extends AbstractRestTest {
    protected static final String[] GAME_IGNORED_FIELDS = {"repositoryId", "websiteName"};

    protected final String basePath;

    protected AbstractGameControllerTest(final String basePath) {
        this.basePath = basePath;
    }

    protected abstract T getGameService();

    protected Game generateGame() {
        return GameUtilities.generateGame(this.getGameService());
    }

    protected Game generateGame(final String gameName) {
        return GameUtilities.generateGame(this.getGameService(), gameName);
    }

    protected MockMvcResponse getInsertResponse(final MockMvcRequestSpecification specification) {
        final String gameName = generateGameName();
        final String websiteName = generateGameName();
        final String cleanName = generateGameName();
        final String categoryName = generateCategoryName();

        return this.getInsertResponse(
                specification,
                gameName,
                websiteName,
                cleanName,
                categoryName
        );
    }

    protected MockMvcResponse getInsertResponse(final MockMvcRequestSpecification specification,
                                                final String gameName,
                                                final String websiteName,
                                                final String cleanName,
                                                final String categoryName) {
        return specification
                .param("websiteName", websiteName)
                .param("cleanName", cleanName)
                .param("categoryName", categoryName)
                .when()
                .put(this.basePath + "/" + gameName);
    }


    @Test
    void getGames() {
        final List<Game> expectedGames = new ArrayList<>();
        for (int count = 0; 10 >= count; count++) {
            expectedGames.add(this.generateGame());
        }

        final List<Game> foundGames = this.parseResponse(
                this.getWithNoApiKey()
                        .when()
                        .get(this.basePath),
                new TypeToken<ArrayList<Game>>() {
                }
        );
        assertThat(foundGames)
                .usingElementComparatorIgnoringFields(GAME_IGNORED_FIELDS)
                .containsAll(expectedGames);
    }

    @Test
    void getGame() {
        final String gameName = generateGameName();
        final Supplier<MockMvcResponse> responseSupplier = () -> this.getWithNoApiKey()
                .when()
                .get(this.basePath + "/" + gameName);

        // Assure that the game does not exist
        this.assertStatus(responseSupplier.get(), HttpStatus.NOT_FOUND);

        // Create game
        final Game game = this.generateGame(gameName);

        // Assure that the game does exist
        final Game gameFound = this.parseResponse(
                responseSupplier.get(),
                Game.class
        );
        assertThat(gameFound)
                .usingRecursiveComparison()
                .ignoringFields(GAME_IGNORED_FIELDS)
                .isEqualTo(game);
    }

    @Test
    void createGame_super_admin_perms() {
        final String gameName = generateGameName();
        final String websiteName = generateGameName();
        final String cleanName = generateGameName();
        final String categoryName = generateCategoryName();

        final Game foundGame = this.parseResponse(
                this.getInsertResponse(
                        this.getWithSuperAdminPrivileges(),
                        gameName,
                        websiteName,
                        cleanName,
                        categoryName
                ),
                Game.class
        );

        assertThat(foundGame.getGameName()).isEqualTo(gameName);
        assertThat(foundGame.getCleanName()).isEqualTo(cleanName);
        assertThat(foundGame.getCategoryName()).isEqualTo(categoryName);
    }

    @Test
    void createGame_status_check_admin() {
        this.assertStatus(
                this.getInsertResponse(this.getWithAdminPrivileges()),
                HttpStatus.OK
        );
    }

    @Test
    void createGame_status_check_user() {
        this.assertStatus(
                this.getInsertResponse(this.getWithUserPrivileges()),
                HttpStatus.FORBIDDEN
        );
    }

    @Test
    void createGame_status_check_no_api_key() {
        this.assertStatus(
                this.getInsertResponse(this.getWithNoApiKey()),
                HttpStatus.UNAUTHORIZED
        );
    }

    @Test
    void getCategories() {
        final List<String> expectedCategories = new ArrayList<>();
        for (int count = 0; 10 >= count; count++) {
            expectedCategories.add(this.generateGame().getCategoryName());
        }

        final List<GameCategory> foundCategories = this.parseResponse(
                this.getWithNoApiKey()
                        .when()
                        .get(this.basePath + "/category"),
                new TypeToken<ArrayList<GameCategory>>() {
                }
        );

        // Map the results to list
        final List<String> foundCategoryNames = foundCategories.stream()
                .map(GameCategory::getCategoryName)
                .collect(Collectors.toList());
        assertThat(foundCategoryNames)
                .containsAll(expectedCategories);
    }

    @Test
    void getCategory() {
        final String gameName = generateGameName();
        final String websiteName = generateGameName();
        final String cleanName = generateGameName();
        final String categoryName = generateCategoryName();

        final Supplier<MockMvcResponse> responseSupplier = () -> this.getWithNoApiKey()
                .when()
                .get(this.basePath + "/category/" + categoryName);

        // Assure that the category does not exist
        this.assertStatus(responseSupplier.get(), HttpStatus.NOT_FOUND);

        // Create category
        final Game game = this.getGameService().getOrCreateGame(websiteName, gameName, cleanName, categoryName);

        // Assure that the category does exist
        final GameCategory categoryFound = this.parseResponse(
                responseSupplier.get(),
                GameCategory.class
        );
        assertThat(categoryFound.getCategoryName()).isEqualTo(game.getCategoryName());
    }
}