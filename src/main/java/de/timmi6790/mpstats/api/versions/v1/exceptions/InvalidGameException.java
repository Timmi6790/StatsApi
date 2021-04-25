package de.timmi6790.mpstats.api.versions.v1.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
@EqualsAndHashCode(callSuper = true)
@Data
public class InvalidGameException extends RuntimeException {
    private static final long serialVersionUID = -6124714303224460427L;

    public InvalidGameException() {
        super("Invalid game");
    }
}
