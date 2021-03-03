package de.timmi6790.mpstats.api.apikey;

import lombok.Data;

import java.util.List;

@Data
public class ApiKeyStorage {
    private final List<ApiKey> apiKeys;
}
