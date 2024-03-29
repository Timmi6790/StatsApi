package de.timmi6790.mpstats.api.versions.v1.common.leaderboard;

import com.google.gson.reflect.TypeToken;
import de.timmi6790.mpstats.api.AbstractRestTest;
import de.timmi6790.mpstats.api.utilities.BoardUtilities;
import de.timmi6790.mpstats.api.utilities.GameUtilities;
import de.timmi6790.mpstats.api.utilities.LeaderboardUtilities;
import de.timmi6790.mpstats.api.utilities.StatUtilities;
import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractLeaderboardControllerTest<L extends LeaderboardService, G extends GameService, S extends StatService, B extends BoardService> extends AbstractRestTest {
    protected static final String[] IGNORED_FIELDS = {"leaderboardRepository", "repositoryId", "websiteName", "game.repositoryId", "game.websiteName", "stat.repositoryId", "stat.websiteName", "board.repositoryId", "board.websiteName"};

    private final String basePath;

    protected AbstractLeaderboardControllerTest(final String basePath) {
        this.basePath = basePath;
    }

    protected abstract L getLeaderboardService();

    protected abstract G getGameService();

    protected abstract S getStatService();

    protected abstract B getBoardService();

    private Game generateGame() {
        return GameUtilities.generateGame(this.getGameService());
    }

    private Board generateBoard() {
        return BoardUtilities.generateBoard(this.getBoardService());
    }

    private Stat generateStat() {
        return StatUtilities.generateStat(this.getStatService());
    }

    protected Leaderboard generateLeaderboard() {
        return LeaderboardUtilities.generateLeaderboard(
                this.getLeaderboardService(),
                this.getGameService(),
                this.getStatService(),
                this.getBoardService()
        );
    }

    protected Leaderboard generateLeaderboard(final Game game, final Stat stat, final Board board) {
        return LeaderboardUtilities.generateLeaderboard(
                this.getLeaderboardService(),
                game,
                stat,
                board
        );
    }

    protected MockMvcResponse getInsertResponse(final MockMvcRequestSpecification specification) {
        final String gameName = this.generateGame().getGameName();
        final String statName = this.generateStat().getStatName();
        final String boardName = this.generateBoard().getBoardName();
        final boolean deprecated = false;

        return this.getInsertResponse(
                specification,
                gameName,
                statName,
                boardName,
                deprecated
        );
    }

    protected MockMvcResponse getInsertResponse(final MockMvcRequestSpecification specification,
                                                final String gameName,
                                                final String statName,
                                                final String boardName,
                                                final boolean deprecated) {
        return specification
                .param("deprecated", deprecated)
                .when()
                .put(this.basePath + "/" + gameName + "/" + statName + "/" + boardName);
    }

    @SneakyThrows
    @Test
    @Disabled("Throwing NestedServlet during github actions")
    void getLeaderboards() {
        final Leaderboard expectedLeaderboard = this.generateLeaderboard();

        final List<Leaderboard> foundLeaderboards = this.parseResponse(
                this.getWithNoApiKey()
                        .when()
                        .get(this.basePath),
                new TypeToken<ArrayList<Leaderboard>>() {
                }
        );

        /*
        assertThat(foundLeaderboards)
                .usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*\\.repositoryId", ".*\\.websiteName")
                .isEqualTo(expectedLeaderboard);

         */

        // TODO: Optimize assertion
        assertThat(foundLeaderboards).isNotEmpty();
    }

    @Test
    void getLeaderboards_by_game() {
        final Game game = this.generateGame();
        final List<Leaderboard> expectedLeaderboards = new ArrayList<>();
        for (int count = 0; 3 > count; count++) {
            final Stat stat = this.generateStat();
            final Board board = this.generateBoard();

            expectedLeaderboards.add(this.generateLeaderboard(game, stat, board));
        }

        final List<Leaderboard> foundLeaderboards = this.parseResponse(
                this.getWithNoApiKey()
                        .when()
                        .get(this.basePath + "/" + game.getGameName()),
                new TypeToken<ArrayList<Leaderboard>>() {
                }
        );

        assertThat(foundLeaderboards).hasSameSizeAs(expectedLeaderboards);
    }

    @Test
    void getLeaderboards_by_game_invalid_game_name() {
        final String gameName = GameUtilities.generateGameName();

        this.assertStatus(
                this.getWithNoApiKey()
                        .when()
                        .get(this.basePath + "/" + gameName),
                HttpStatus.NOT_FOUND
        );
    }

    @Test
    void getLeaderboards_by_game_stat() {
        final Game game = this.generateGame();
        final Stat stat = this.generateStat();
        final List<Leaderboard> expectedLeaderboards = new ArrayList<>();
        for (int count = 0; 3 > count; count++) {
            final Board board = this.generateBoard();

            expectedLeaderboards.add(this.generateLeaderboard(game, stat, board));
        }

        final List<Leaderboard> foundLeaderboards = this.parseResponse(
                this.getWithNoApiKey()
                        .when()
                        .get(this.basePath + "/" + game.getGameName() + "/" + stat.getStatName()),
                new TypeToken<ArrayList<Leaderboard>>() {
                }
        );

        assertThat(foundLeaderboards).hasSameSizeAs(expectedLeaderboards);
    }

    @Test
    void getLeaderboards_by_game_stat_invalid_game_name() {
        final String gameName = GameUtilities.generateGameName();
        final Stat stat = this.generateStat();

        this.assertStatus(
                this.getWithNoApiKey()
                        .when()
                        .get(this.basePath + "/" + gameName + "/" + stat.getStatName()),
                HttpStatus.NOT_FOUND
        );
    }

    @Test
    void getLeaderboards_by_game_stat_invalid_stat_name() {
        final Game game = this.generateGame();
        final String statName = StatUtilities.generateStatName();

        this.assertStatus(
                this.getWithNoApiKey()
                        .when()
                        .get(this.basePath + "/" + game.getGameName() + "/" + statName),
                HttpStatus.NOT_FOUND
        );
    }

    @Test
    void getLeaderboard() {
        final Game game = this.generateGame();
        final Stat stat = this.generateStat();
        final Board board = this.generateBoard();

        final Supplier<MockMvcResponse> responseSupplier = () -> this.getWithNoApiKey()
                .when()
                .get(this.basePath + "/" + game.getGameName() + "/" + stat.getStatName() + "/" + board.getBoardName());

        // Assure that the leaderboard does not exist
        this.assertStatus(responseSupplier.get(), HttpStatus.NOT_FOUND);

        // Create leaderboard
        final Leaderboard leaderboard = this.generateLeaderboard(game, stat, board);

        // Assure that the leaderboard does exist
        final Leaderboard boardFound = this.parseResponse(
                responseSupplier.get(),
                Leaderboard.class
        );

        assertThat(boardFound)
                .usingRecursiveComparison()
                .ignoringFields(IGNORED_FIELDS)
                .ignoringFields("lastSaveTime", "lastCacheSaveTime")
                .isEqualTo(leaderboard);
        assertThat(boardFound.getLastSaveTime()).isEqualTo(leaderboard.getLastSaveTime());
        assertThat(boardFound.getLastCacheSaveTime()).isEqualTo(leaderboard.getLastCacheSaveTime());
    }

    @Test
    void createLeaderboard_super_admin_perms() {
        final Game game = this.generateGame();
        final Stat stat = this.generateStat();
        final Board board = this.generateBoard();
        final boolean deprecated = true;

        final Leaderboard foundLeaderboard = this.parseResponse(
                this.getInsertResponse(
                        this.getWithSuperAdminPrivileges(),
                        game.getGameName(),
                        stat.getStatName(),
                        board.getBoardName(),
                        deprecated
                ),
                Leaderboard.class
        );

        assertThat(foundLeaderboard.getGame())
                .usingRecursiveComparison()
                .ignoringFields(IGNORED_FIELDS)
                .isEqualTo(game);

        assertThat(foundLeaderboard.getStat())
                .usingRecursiveComparison()
                .ignoringFields(IGNORED_FIELDS)
                .isEqualTo(stat);

        assertThat(foundLeaderboard.getBoard())
                .usingRecursiveComparison()
                .ignoringFields(IGNORED_FIELDS)
                .isEqualTo(board);

        assertThat(foundLeaderboard.isDeprecated()).isEqualTo(deprecated);
    }

    @Test
    void createLeaderboard_status_check_admin() {
        this.assertStatus(
                this.getInsertResponse(this.getWithAdminPrivileges()),
                HttpStatus.OK
        );
    }

    @Test
    void createLeaderboard_status_check_user() {
        this.assertStatus(
                this.getInsertResponse(this.getWithUserPrivileges()),
                HttpStatus.FORBIDDEN
        );
    }

    @Test
    void createLeaderboard_status_check_no_api_key() {
        this.assertStatus(
                this.getInsertResponse(this.getWithNoApiKey()),
                HttpStatus.UNAUTHORIZED
        );
    }
}