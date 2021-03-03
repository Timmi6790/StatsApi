package de.timmi6790.mineplex_stats_api.apikey;

import lombok.Data;

import java.util.List;

@Data
public class ApiKeyStorage {
    private final List<ApiKey> apiKeys;
}
