package de.timmi6790.mpstats.api.versions.v1.common.player.exceptions;

import de.timmi6790.mpstats.api.exceptions.RestException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InvalidPlayerNameRestException extends RestException {
    @Serial
    private static final long serialVersionUID = -142522590557598765L;

    public InvalidPlayerNameRestException() {
        super("player-1", "Invalid player name");
    }
}
