package de.timmi6790.mpstats.api.apikey.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ApiKeyStorage {
    private final Collection<ApiKey> apiKeys;

    public ApiKeyStorage(final ApiKey... apiKeys) {
        this.apiKeys = Arrays.asList(apiKeys);
    }
}
