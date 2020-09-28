package de.timmi6790.mineplex_stats_api.apikey;

import lombok.Data;

import java.util.List;

@Data
public class ApiKeySave {
    private final List<ApiKey> apiKeys;
}
