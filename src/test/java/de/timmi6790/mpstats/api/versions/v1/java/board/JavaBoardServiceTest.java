package de.timmi6790.mpstats.api.versions.v1.java.board;

import de.timmi6790.mpstats.api.AbstractIntegrationTest;
import de.timmi6790.mpstats.api.versions.v1.java.board.repository.JavaBoardRepository;
import de.timmi6790.mpstats.api.versions.v1.java.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.java.board.repository.postgres.JavaBoardPostgresRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class JavaBoardServiceTest {
    private static JavaBoardRepository javaBoardRepository;
    private static JavaBoardService javaBoardService;

    private static final AtomicInteger BOARD_ID = new AtomicInteger(0);

    @BeforeAll
    static void setUp() {
        javaBoardRepository = new JavaBoardPostgresRepository(AbstractIntegrationTest.jdbi());
        javaBoardService = new JavaBoardService(javaBoardRepository);
    }

    private String generateBoardName() {
        return "BOARD" + BOARD_ID.incrementAndGet();
    }

    private Board generateBoard(final String boardName) {
        final String websiteName = this.generateBoardName();
        final String cleanName = this.generateBoardName();
        final int time = ThreadLocalRandom.current().nextInt(5_000);

        return javaBoardService.getBordOrCreate(websiteName, boardName, cleanName, time);
    }

    @Test
    void hasBoard() {
        final String boardName = this.generateBoardName();

        final boolean boardNotFound = javaBoardService.hasBoard(boardName);
        assertThat(boardNotFound).isFalse();

        this.generateBoard(boardName);

        final boolean boardFound = javaBoardService.hasBoard(boardName);
        assertThat(boardFound).isTrue();
    }

    @Test
    void hasBoard_case_insensitive() {
        final String boardName = this.generateBoardName();
        this.generateBoard(boardName);

        final boolean boardFoundLower = javaBoardService.hasBoard(boardName.toLowerCase());
        assertThat(boardFoundLower).isTrue();

        final boolean boardFoundUpper = javaBoardService.hasBoard(boardName.toUpperCase());
        assertThat(boardFoundUpper).isTrue();
    }

    @Test
    void getBoards() {
        final Board board1 = this.generateBoard(this.generateBoardName());
        final Board board2 = this.generateBoard(this.generateBoardName());

        final List<Board> board = javaBoardService.getBoards();
        assertThat(board).containsAll(Arrays.asList(board1, board2));
    }

    @Test
    void getBoard_case_insensitive() {
        final String boardName = this.generateBoardName();
        this.generateBoard(boardName);

        final Optional<Board> boardFoundLower = javaBoardService.getBoard(boardName.toLowerCase());
        assertThat(boardFoundLower).isPresent();

        final Optional<Board> boardFoundUpper = javaBoardService.getBoard(boardName.toUpperCase());
        assertThat(boardFoundUpper).isPresent();

        assertThat(boardFoundLower).contains(boardFoundUpper.get());
    }

    @Test
    void createBoard() {
        final String websiteName = this.generateBoardName();
        final String cleanName = this.generateBoardName();
        final String boardName = this.generateBoardName();
        final int updateTime = 1;

        final Optional<Board> boardNotFound = javaBoardService.getBoard(boardName);
        assertThat(boardNotFound).isNotPresent();

        final Board createdBoard = javaBoardService.getBordOrCreate(websiteName, boardName, cleanName, updateTime);
        assertThat(createdBoard.getWebsiteName()).isEqualTo(websiteName);
        assertThat(createdBoard.getCleanName()).isEqualTo(cleanName);
        assertThat(createdBoard.getBoardName()).isEqualTo(boardName);
        assertThat(createdBoard.getUpdateTime()).isEqualTo(updateTime);

        final Optional<Board> boardFound = javaBoardService.getBoard(boardName);
        assertThat(boardFound)
                .isPresent()
                .contains(createdBoard);

        final Optional<Board> boardCacheFound = javaBoardRepository.getBoard(boardName);
        assertThat(boardCacheFound)
                .isPresent()
                .contains(createdBoard);
    }

    @Test
    void createBoard_duplicate() {
        final String websiteName = this.generateBoardName();
        final String cleanName = this.generateBoardName();
        final String boardName = this.generateBoardName();
        final int updateTime = 1;

        final Board board1 = javaBoardService.getBordOrCreate(websiteName, boardName, cleanName, updateTime);
        final Board board2 = javaBoardService.getBordOrCreate(websiteName, boardName, cleanName, updateTime);

        assertThat(board1).isEqualTo(board2);
    }

    @Test
    void deleteBoard() {
        final String boardName = this.generateBoardName();
        this.generateBoard(boardName);

        javaBoardService.deleteBoard(boardName);

        final boolean notFound = javaBoardService.hasBoard(boardName);
        assertThat(notFound).isFalse();

        final Optional<Board> boardNotFound = javaBoardService.getBoard(boardName);
        assertThat(boardNotFound).isNotPresent();

        final Optional<Board> boardCacheNotFound = javaBoardRepository.getBoard(boardName);
        assertThat(boardCacheNotFound).isNotPresent();
    }

    @Test
    void deleteBoard_case_insensitive() {
        final String boardName = this.generateBoardName();
        this.generateBoard(boardName);

        javaBoardService.deleteBoard(boardName.toLowerCase());

        final boolean notFound = javaBoardService.hasBoard(boardName);
        assertThat(notFound).isFalse();

        final Optional<Board> boardNotFound = javaBoardService.getBoard(boardName);
        assertThat(boardNotFound).isNotPresent();
    }

    @Test
    void innit_with_existing_boards() {
        final String boardName = this.generateBoardName();
        final Board board = this.generateBoard(boardName);

        final JavaBoardService newJavaBoardService = new JavaBoardService(javaBoardRepository);

        final boolean foundBoard = newJavaBoardService.hasBoard(boardName);
        assertThat(foundBoard).isTrue();

        final Optional<Board> boardFound = newJavaBoardService.getBoard(boardName);
        assertThat(boardFound)
                .isPresent()
                .contains(board);
    }
}