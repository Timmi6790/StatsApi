package de.timmi6790.mpstats.api.apikey.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ApiKey {
    public static ApiKey of(final int dailyRateLimit, final int minuteRateLimit) {
        return new ApiKey(
                UUID.randomUUID(),
                new RateLimit(dailyRateLimit, minuteRateLimit),
                new ArrayList<>()
        );
    }

    private final UUID key;
    private final RateLimit rateLimit;
    private final List<String> authorities;

    /**
     * Default constructor for GSON
     */
    public ApiKey() {
        this.key = null;
        this.rateLimit = new RateLimit(1, 1);
        this.authorities = new ArrayList<>();
    }

    public List<String> getAuthorities() {
        if (this.authorities == null) {
            return new ArrayList<>();
        }
        return this.authorities;
    }
}
