package de.timmi6790.mpstats.api.apikey.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ApiKey {
    public static ApiKey of(final int dailyRateLimit, final int minuteRateLimit) {
        return new ApiKey(
                UUID.randomUUID(),
                new RateLimit(dailyRateLimit, minuteRateLimit),
                new String[0]
        );
    }

    private final UUID key;
    private final RateLimit rateLimit;
    private final String[] authorities;

    /**
     * Default constructor for GSON
     */
    public ApiKey() {
        this.key = null;
        this.rateLimit = new RateLimit(1, 1);
        this.authorities = null;
    }

    public String[] getAuthorities() {
        if (this.authorities == null) {
            return new String[0];
        }
        return this.authorities;
    }
}
