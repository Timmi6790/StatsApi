package de.timmi6790.mpstats.api.versions.v1.common.board;


import de.timmi6790.mpstats.api.versions.v1.common.board.exceptions.InvalidBoardNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.utilities.RestUtilities;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

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
    public Board getBoard(@PathVariable final String boardName) throws InvalidBoardNameRestException {
        return RestUtilities.getBoardOrThrow(this.boardService, boardName);
    }
}
