package de.timmi6790.mpstats.api.apikey;

import de.timmi6790.commons.utilities.GsonUtilities;
import de.timmi6790.mpstats.api.apikey.models.ApiKey;
import de.timmi6790.mpstats.api.apikey.models.ApiKeyStorage;
import de.timmi6790.mpstats.api.apikey.models.RateLimit;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Log
@Service
public class ApiKeyService {
    private final Map<UUID, ApiKey> apiKeys = new ConcurrentHashMap<>();
    private final Path apiKeyPath;

    public ApiKeyService() {
        this.apiKeyPath = Paths.get("./apiKeys.json");

        this.setUpStorage();
        this.loadKeysFromStorage();
    }

    private void saveToFile(final ApiKeyStorage apiKeyStorage) {
        GsonUtilities.saveToJson(this.apiKeyPath, apiKeyStorage);
    }

    private void setUpStorage() {
        if (!this.apiKeyPath.toFile().exists()) {
            log.info("Create new master api key");
            this.saveToFile(
                    new ApiKeyStorage(
                            new ApiKey(
                                    UUID.randomUUID(),
                                    new RateLimit(1_000_000, 1_000_000),
                                    new String[]{"SUPERADMIN"}
                            )
                    )
            );
        }
    }

    private void loadKeysFromStorage() {
        final ApiKeyStorage storage = GsonUtilities.readJsonFile(this.apiKeyPath, ApiKeyStorage.class);
        // Save the changes
        this.saveToFile(storage);
        for (final ApiKey apiKey : storage.getApiKeys()) {
            this.apiKeys.put(apiKey.getKey(), apiKey);
        }
    }

    private Optional<UUID> getUUID(final String input) {
        if (input == null || input.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(UUID.fromString(input));
        } catch (final IllegalArgumentException ignore) {
            return Optional.empty();
        }
    }

    public void addApiKey(final ApiKey apiKey) {
        if (!this.apiKeys.containsKey(apiKey.getKey())) {
            this.apiKeys.put(apiKey.getKey(), apiKey);
            this.saveToFile(new ApiKeyStorage(this.apiKeys.values()));
        }
    }

    public Optional<ApiKey> getApiKey(final String apiKey) {
        return this.getUUID(apiKey)
                .map(this.apiKeys::get);
    }
}
