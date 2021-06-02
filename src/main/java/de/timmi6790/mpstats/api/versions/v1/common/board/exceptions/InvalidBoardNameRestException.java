package de.timmi6790.mpstats.api.versions.v1.common.board.exceptions;

import de.timmi6790.mpstats.api.exceptions.RestException;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.List;
import java.util.Map;

@ResponseStatus(HttpStatus.NOT_FOUND)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InvalidBoardNameRestException extends RestException {
    @Serial
    private static final long serialVersionUID = -4901470227202345352L;

    private final List<Board> suggestedBoards;

    public InvalidBoardNameRestException(final List<Board> suggestedBoards) {
        super("board-1", "Invalid board name");

        this.suggestedBoards = suggestedBoards;
    }

    @Override
    public Map<String, Object> getErrorAttributes() {
        final Map<String, Object> errorAttributes = super.getErrorAttributes();

        errorAttributes.put("suggestedBoards", this.suggestedBoards);

        return errorAttributes;
    }
}
