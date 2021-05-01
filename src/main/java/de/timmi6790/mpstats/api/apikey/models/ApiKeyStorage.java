package de.timmi6790.mpstats.api.apikey.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.Collection;

@Data
@AllArgsConstructor
public class ApiKeyStorage {
    private final Collection<ApiKey> apiKeys;

    public ApiKeyStorage(final ApiKey... apiKeys) {
        this.apiKeys = Arrays.asList(apiKeys);
    }
}
