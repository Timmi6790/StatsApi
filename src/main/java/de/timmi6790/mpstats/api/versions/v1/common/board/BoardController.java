package de.timmi6790.mpstats.api.versions.v1.common.board;


import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

public abstract class BoardController {
    @Getter(value = AccessLevel.PROTECTED)
    private final BoardService boardService;

    protected BoardController(final BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping
    @Operation(summary = "Find all available boards")
    public List<Board> getBoards() {
        return this.boardService.getBoards();
    }

    @GetMapping(value = "/{boardName}")
    @Operation(summary = "Find board by name")
    public Optional<Board> getBoard(@PathVariable final String boardName) {
        return this.boardService.getBoard(boardName);
    }

    @PostMapping(value = "/{boardName}")
    @Operation(summary = "Create a new board")
    public Board createBoard(@PathVariable final String boardName,
                             @RequestParam final String websiteName,
                             @RequestParam final String cleanName,
                             @RequestParam final int updateTime) {
        // TODO: Add spring security
        return this.boardService.getBoardOrCreate(websiteName, boardName, cleanName, updateTime);
    }
}