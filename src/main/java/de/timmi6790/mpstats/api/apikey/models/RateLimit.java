package de.timmi6790.mpstats.api.apikey.models;

import lombok.Data;

@Data
public class RateLimit {
    private final int daily;
    private final int minute;

    public RateLimit(final int daily, final int minute) {
        if (daily < 1) {
            throw new IllegalArgumentException("Daily can't be lower than 1");
        }
        if (minute < 1) {
            throw new IllegalArgumentException("Minute can't be lower than 1");
        }

        this.daily = daily;
        this.minute = minute;
    }
}
