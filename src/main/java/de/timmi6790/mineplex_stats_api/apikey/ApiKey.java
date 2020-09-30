package de.timmi6790.mineplex_stats_api.apikey;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ApiKey {
    private final UUID key;
    private final int dailyRateLimit;
    private final int minuteRateLimitLimit;
    private final List<String> permissions;
}
