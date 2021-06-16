package de.timmi6790.mpstats.api.apikey.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
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

    public String[] getAuthorities() {
        if (this.authorities == null) {
            return new String[0];
        }
        return this.authorities;
    }
}
