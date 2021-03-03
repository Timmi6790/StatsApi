package de.timmi6790.mineplex_stats_api.apikey;

import de.timmi6790.commons.utilities.GsonUtilities;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Log
@Service
public class ApiKeyService {
    private final Map<UUID, ApiKey> apiKeys = new ConcurrentHashMap<>();
    private final Path apiKeyPath;

    public ApiKeyService() {
        final Path basePath = Paths.get(".").toAbsolutePath().normalize();
        this.apiKeyPath = Paths.get(basePath + "/apiKeys.json");

        this.setUpStorage();
        this.loadKeysFromStorage();
    }

    private void setUpStorage() {
        if (!this.apiKeyPath.toFile().exists()) {
            log.info("Created new master api key");
            GsonUtilities.saveToJson(
                    this.apiKeyPath,
                    new ApiKeyStorage(Collections.singletonList(
                            new ApiKey(
                                    UUID.randomUUID(),
                                    1_000_000,
                                    1_000_000,
                                    new ArrayList<>()
                            )
                    ))
            );
        }
    }

    private void loadKeysFromStorage() {
        final ApiKeyStorage save = GsonUtilities.readJsonFile(this.apiKeyPath, ApiKeyStorage.class);
        for (final ApiKey apiKey : save.getApiKeys()) {
            this.apiKeys.put(apiKey.getKey(), apiKey);
        }
    }

    private boolean isValidUUID(final String input) {
        try {
            UUID.fromString(input);
            return true;
        } catch (final IllegalArgumentException ignore) {
            return false;
        }
    }

    public void addApiKey(final ApiKey apiKey) {
        this.apiKeys.put(apiKey.getKey(), apiKey);
        GsonUtilities.saveToJson(this.apiKeyPath, new ApiKeyStorage(new ArrayList<>(this.apiKeys.values())));
    }

    public Optional<ApiKey> getApiKey(final String apiKey) {
        if (this.isValidUUID(apiKey)) {
            return Optional.ofNullable(this.apiKeys.get(UUID.fromString(apiKey)));
        }
        return Optional.empty();
    }
}
