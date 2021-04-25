package de.timmi6790.mpstats.api.versions.v1.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class StatsNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 5686163533773128208L;

    public StatsNotFoundException() {
        super("No stats found");
    }
}
