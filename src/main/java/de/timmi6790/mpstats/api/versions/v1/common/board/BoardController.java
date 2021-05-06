package de.timmi6790.mpstats.api.versions.v1.common.board;


import de.timmi6790.mpstats.api.security.annontations.RequireAdminPerms;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Getter(AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BoardController {
    private final BoardService boardService;

    @GetMapping
    @Operation(summary = "Find all available boards")
    public List<Board> getBoards() {
        return this.boardService.getBoards();
    }

    @GetMapping("/{boardName}")
    @Operation(summary = "Find board by name")
    public Optional<Board> getBoard(@PathVariable final String boardName) {
        return this.boardService.getBoard(boardName);
    }

    @PutMapping("/{boardName}")
    @Operation(summary = "Create a new board")
    @RequireAdminPerms
    public Board createBoard(@PathVariable final String boardName,
                             @RequestParam final String websiteName,
                             @RequestParam final String cleanName,
                             @RequestParam final int updateTime) {
        return this.boardService.getBoardOrCreate(websiteName, boardName, cleanName, updateTime);
    }
}
