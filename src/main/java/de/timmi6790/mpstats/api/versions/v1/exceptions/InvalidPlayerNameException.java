package de.timmi6790.mpstats.api.versions.v1.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
@EqualsAndHashCode(callSuper = true)
@Data
public class InvalidPlayerNameException extends RuntimeException {
    private static final long serialVersionUID = -338414523159595050L;
    private final String playerName;

    public InvalidPlayerNameException(final String playerName) {
        super("Invalid player name");

        this.playerName = playerName;
    }
}
