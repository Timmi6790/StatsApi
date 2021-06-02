package de.timmi6790.mpstats.api.versions.v1.common.game.exceptions;

import de.timmi6790.mpstats.api.exceptions.RestException;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
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
public class InvalidGameNameRestException extends RestException {
    @Serial
    private static final long serialVersionUID = -6124714303224460427L;

    private final List<Game> suggestedGames;

    public InvalidGameNameRestException(final List<Game> suggestedGames) {
        super("game-1", "Invalid game name");

        this.suggestedGames = suggestedGames;
    }

    @Override
    public Map<String, Object> getErrorAttributes() {
        final Map<String, Object> errorAttributes = super.getErrorAttributes();

        errorAttributes.put("suggestedGames", this.suggestedGames);

        return errorAttributes;
    }
}
