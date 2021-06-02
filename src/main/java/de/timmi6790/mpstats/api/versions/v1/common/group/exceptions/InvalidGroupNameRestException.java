package de.timmi6790.mpstats.api.versions.v1.common.group.exceptions;

import de.timmi6790.mpstats.api.exceptions.RestException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InvalidGroupNameRestException extends RestException {
    @Serial
    private static final long serialVersionUID = 230808094405074156L;

    public InvalidGroupNameRestException() {
        super("group-1", "Invalid group name");
    }
}
