package de.timmi6790.mpstats.api.versions.v1.common.board;

import com.google.gson.reflect.TypeToken;
import de.timmi6790.mpstats.api.AbstractRestTest;
import de.timmi6790.mpstats.api.utilities.BoardUtilities;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static de.timmi6790.mpstats.api.utilities.BoardUtilities.generateBoardName;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractBoardControllerTest<T extends BoardService> extends AbstractRestTest {
    protected static final String[] BOARD_IGNORED_FIELDS = {"repositoryId", "websiteName"};

    private final String basePath;

    protected AbstractBoardControllerTest(final String basePath) {
        this.basePath = basePath;
    }

    protected abstract T getBoardService();

    protected Board generateBoard() {
        return BoardUtilities.generateBoard(this.getBoardService());
    }

    protected Board generateBoard(final String boardName) {
        return BoardUtilities.generateBoard(this.getBoardService(), boardName);
    }

    protected MockMvcResponse getInsertResponse(final MockMvcRequestSpecification specification) {
        final String boardName = generateBoardName();
        final String websiteName = generateBoardName();
        final String cleanName = generateBoardName();
        final int updateTime = 9000;

        return this.getInsertResponse(
                specification,
                boardName,
                websiteName,
                cleanName,
                updateTime
        );
    }

    protected MockMvcResponse getInsertResponse(final MockMvcRequestSpecification specification,
                                                final String boardName,
                                                final String websiteName,
                                                final String cleanName,
                                                final int updateTime) {
        return specification
                .param("websiteName", websiteName)
                .param("cleanName", cleanName)
                .param("updateTime", updateTime)
                .when()
                .put(this.basePath + "/" + boardName);
    }

    @SneakyThrows
    @Test
    void getBoards() {
        final List<Board> expectedBoards = new ArrayList<>();
        for (int count = 0; 10 >= count; count++) {
            expectedBoards.add(this.generateBoard());
        }

        final List<Board> foundBoards = this.parseResponse(
                this.getWithNoApiKey()
                        .when()
                        .get(this.basePath),
                new TypeToken<ArrayList<Board>>() {
                }
        );
        assertThat(foundBoards)
                .usingElementComparatorIgnoringFields(BOARD_IGNORED_FIELDS)
                .containsAll(expectedBoards);
    }

    @Test
    void getBoard() {
        final String boardName = generateBoardName();
        final Supplier<MockMvcResponse> responseSupplier = () -> this.getWithNoApiKey()
                .when()
                .get(this.basePath + "/" + boardName);

        // Assure that the board does not exist
        this.assertStatus(responseSupplier.get(), HttpStatus.NOT_FOUND);

        // Create board
        final Board board = this.generateBoard(boardName);

        // Assure that the board does exist
        final Board boardFound = this.parseResponse(
                responseSupplier.get(),
                Board.class
        );
        assertThat(boardFound)
                .usingRecursiveComparison()
                .ignoringFields(BOARD_IGNORED_FIELDS)
                .isEqualTo(board);
    }

    @Test
    void createBoard_super_admin_perms() {
        final String boardName = generateBoardName();
        final String websiteName = generateBoardName();
        final String cleanName = generateBoardName();
        final int updateTime = 9000;

        final Board foundBoard = this.parseResponse(
                this.getInsertResponse(
                        this.getWithSuperAdminPrivileges(),
                        boardName,
                        websiteName,
                        cleanName,
                        updateTime
                ),
                Board.class
        );

        assertThat(foundBoard.getBoardName()).isEqualTo(boardName);
        assertThat(foundBoard.getCleanName()).isEqualTo(cleanName);
        assertThat(foundBoard.getUpdateTime()).isEqualTo(updateTime);
    }

    @Test
    void createBoard_status_check_admin() {
        this.assertStatus(
                this.getInsertResponse(this.getWithAdminPrivileges()),
                HttpStatus.OK
        );
    }

    @Test
    void createBoard_status_check_user() {
        this.assertStatus(
                this.getInsertResponse(this.getWithUserPrivileges()),
                HttpStatus.FORBIDDEN
        );
    }

    @Test
    void createBoard_status_check_no_api_key() {
        this.assertStatus(
                this.getInsertResponse(this.getWithNoApiKey()),
                HttpStatus.UNAUTHORIZED
        );
    }
}