package de.timmi6790.mpstats.api.versions.v1.common.board;

import de.timmi6790.mpstats.api.utilities.BoardUtilities;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.BoardRepository;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static de.timmi6790.mpstats.api.utilities.BoardUtilities.generateBoardName;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractBoardServiceTest {
    private final Supplier<BoardService> boardServiceSupplier;
    private final BoardRepository boardRepository;
    private final BoardService boardService;

    public AbstractBoardServiceTest(final Supplier<BoardService> boardServiceSupplier) {
        this.boardServiceSupplier = boardServiceSupplier;
        this.boardService = boardServiceSupplier.get();
        this.boardRepository = this.boardService.getBoardRepository();
    }

    protected Board generateBoard(final String boardName) {
        return BoardUtilities.generateBoard(this.boardService, boardName);
    }

    protected Board generateBoard() {
        return BoardUtilities.generateBoard(this.boardService);
    }

    @Test
    void hasBoard() {
        final String boardName = generateBoardName();

        final boolean boardNotFound = this.boardService.hasBoard(boardName);
        assertThat(boardNotFound).isFalse();

        this.generateBoard(boardName);

        final boolean boardFound = this.boardService.hasBoard(boardName);
        assertThat(boardFound).isTrue();
    }

    @Test
    void hasBoard_case_insensitive() {
        final String boardName = BoardUtilities.generateBoardName();
        this.generateBoard(boardName);

        final boolean boardFoundLower = this.boardService.hasBoard(boardName.toLowerCase());
        assertThat(boardFoundLower).isTrue();

        final boolean boardFoundUpper = this.boardService.hasBoard(boardName.toUpperCase());
        assertThat(boardFoundUpper).isTrue();
    }

    @Test
    void getBoards() {
        final Board board1 = this.generateBoard();
        final Board board2 = this.generateBoard(generateBoardName());

        final List<Board> board = this.boardService.getBoards();
        assertThat(board).containsAll(Arrays.asList(board1, board2));
    }

    @Test
    void getBoard_case_insensitive() {
        final String boardName = generateBoardName();
        this.generateBoard(boardName);

        final Optional<Board> boardFoundLower = this.boardService.getBoard(boardName.toLowerCase());
        assertThat(boardFoundLower).isPresent();

        final Optional<Board> boardFoundUpper = this.boardService.getBoard(boardName.toUpperCase());
        assertThat(boardFoundUpper).isPresent();

        assertThat(boardFoundLower).contains(boardFoundUpper.get());
    }

    @Test
    void createBoard() {
        final String websiteName = generateBoardName();
        final String cleanName = generateBoardName();
        final String boardName = generateBoardName();
        final int updateTime = 1;

        final Optional<Board> boardNotFound = this.boardService.getBoard(boardName);
        assertThat(boardNotFound).isNotPresent();

        final Board createdBoard = this.boardService.getBoardOrCreate(websiteName, boardName, cleanName, updateTime);
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
        final String websiteName = generateBoardName();
        final String cleanName = generateBoardName();
        final String boardName = generateBoardName();
        final int updateTime = 1;

        final Board board1 = this.boardService.getBoardOrCreate(websiteName, boardName, cleanName, updateTime);
        final Board board2 = this.boardService.getBoardOrCreate(websiteName, boardName, cleanName, updateTime);

        assertThat(board1).isEqualTo(board2);
    }

    @Test
    void deleteBoard() {
        final String boardName = generateBoardName();
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
        final String boardName = generateBoardName();
        this.generateBoard(boardName);

        this.boardService.deleteBoard(boardName.toLowerCase());

        final boolean notFound = this.boardService.hasBoard(boardName);
        assertThat(notFound).isFalse();

        final Optional<Board> boardNotFound = this.boardService.getBoard(boardName);
        assertThat(boardNotFound).isNotPresent();
    }

    @Test
    void innit_with_existing_boards() {
        final String boardName = generateBoardName();
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