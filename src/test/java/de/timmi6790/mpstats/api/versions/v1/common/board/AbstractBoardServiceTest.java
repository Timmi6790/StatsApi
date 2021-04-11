package de.timmi6790.mpstats.api.versions.v1.common.board;

import de.timmi6790.mpstats.api.versions.v1.common.board.repository.BoardRepository;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractBoardServiceTest {
    private static final AtomicInteger BOARD_ID = new AtomicInteger(0);

    private final Supplier<BoardService> boardServiceSupplier;
    private final BoardRepository boardRepository;
    private final BoardService boardService;

    public AbstractBoardServiceTest(final Supplier<BoardService> boardServiceSupplier) {
        this.boardServiceSupplier = boardServiceSupplier;
        this.boardService = boardServiceSupplier.get();
        this.boardRepository = this.boardService.getBoardRepository();
    }

    private String generateBoardName() {
        return "BOARD" + BOARD_ID.incrementAndGet();
    }

    private Board generateBoard(final String boardName) {
        final String websiteName = this.generateBoardName();
        final String cleanName = this.generateBoardName();
        final int time = ThreadLocalRandom.current().nextInt(5_000);

        return this.boardService.getBordOrCreate(websiteName, boardName, cleanName, time);
    }

    @Test
    void hasBoard() {
        final String boardName = this.generateBoardName();

        final boolean boardNotFound = this.boardService.hasBoard(boardName);
        assertThat(boardNotFound).isFalse();

        this.generateBoard(boardName);

        final boolean boardFound = this.boardService.hasBoard(boardName);
        assertThat(boardFound).isTrue();
    }

    @Test
    void hasBoard_case_insensitive() {
        final String boardName = this.generateBoardName();
        this.generateBoard(boardName);

        final boolean boardFoundLower = this.boardService.hasBoard(boardName.toLowerCase());
        assertThat(boardFoundLower).isTrue();

        final boolean boardFoundUpper = this.boardService.hasBoard(boardName.toUpperCase());
        assertThat(boardFoundUpper).isTrue();
    }

    @Test
    void getBoards() {
        final Board board1 = this.generateBoard(this.generateBoardName());
        final Board board2 = this.generateBoard(this.generateBoardName());

        final List<Board> board = this.boardService.getBoards();
        assertThat(board).containsAll(Arrays.asList(board1, board2));
    }

    @Test
    void getBoard_case_insensitive() {
        final String boardName = this.generateBoardName();
        this.generateBoard(boardName);

        final Optional<Board> boardFoundLower = this.boardService.getBoard(boardName.toLowerCase());
        assertThat(boardFoundLower).isPresent();

        final Optional<Board> boardFoundUpper = this.boardService.getBoard(boardName.toUpperCase());
        assertThat(boardFoundUpper).isPresent();

        assertThat(boardFoundLower).contains(boardFoundUpper.get());
    }

    @Test
    void createBoard() {
        final String websiteName = this.generateBoardName();
        final String cleanName = this.generateBoardName();
        final String boardName = this.generateBoardName();
        final int updateTime = 1;

        final Optional<Board> boardNotFound = this.boardService.getBoard(boardName);
        assertThat(boardNotFound).isNotPresent();

        final Board createdBoard = this.boardService.getBordOrCreate(websiteName, boardName, cleanName, updateTime);
        assertThat(createdBoard.getWebsiteName()).isEqualTo(websiteName);
        assertThat(createdBoard.getCleanName()).isEqualTo(cleanName);
        assertThat(createdBoard.getBoardName()).isEqualTo(boardName);
        assertThat(createdBoard.getUpdateTime()).isEqualTo(updateTime);

        final Optional<Board> boardFound = this.boardService.getBoard(boardName);
        assertThat(boardFound)
                .isPresent()
                .contains(createdBoard);

        final Optional<Board> boardCacheFound = this.boardRepository.getBoard(boardName);
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

        final Board board1 = this.boardService.getBordOrCreate(websiteName, boardName, cleanName, updateTime);
        final Board board2 = this.boardService.getBordOrCreate(websiteName, boardName, cleanName, updateTime);

        assertThat(board1).isEqualTo(board2);
    }

    @Test
    void deleteBoard() {
        final String boardName = this.generateBoardName();
        this.generateBoard(boardName);

        this.boardService.deleteBoard(boardName);

        final boolean notFound = this.boardService.hasBoard(boardName);
        assertThat(notFound).isFalse();

        final Optional<Board> boardNotFound = this.boardService.getBoard(boardName);
        assertThat(boardNotFound).isNotPresent();

        final Optional<Board> boardCacheNotFound = this.boardRepository.getBoard(boardName);
        assertThat(boardCacheNotFound).isNotPresent();
    }

    @Test
    void deleteBoard_case_insensitive() {
        final String boardName = this.generateBoardName();
        this.generateBoard(boardName);

        this.boardService.deleteBoard(boardName.toLowerCase());

        final boolean notFound = this.boardService.hasBoard(boardName);
        assertThat(notFound).isFalse();

        final Optional<Board> boardNotFound = this.boardService.getBoard(boardName);
        assertThat(boardNotFound).isNotPresent();
    }

    @Test
    void innit_with_existing_boards() {
        final String boardName = this.generateBoardName();
        final Board board = this.generateBoard(boardName);

        final BoardService newBoardService = this.boardServiceSupplier.get();

        final boolean foundBoard = newBoardService.hasBoard(boardName);
        assertThat(foundBoard).isTrue();

        final Optional<Board> boardFound = newBoardService.getBoard(boardName);
        assertThat(boardFound)
                .isPresent()
                .contains(board);
    }
}