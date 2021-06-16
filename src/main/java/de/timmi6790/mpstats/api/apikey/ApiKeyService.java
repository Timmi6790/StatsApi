package de.timmi6790.mpstats.api.apikey;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.timmi6790.mpstats.api.apikey.models.ApiKey;
import de.timmi6790.mpstats.api.apikey.models.ApiKeyStorage;
import de.timmi6790.mpstats.api.apikey.models.RateLimit;
import de.timmi6790.mpstats.api.utilities.FileUtilities;
import io.sentry.Sentry;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Service
public class ApiKeyService {
    private final Map<UUID, ApiKey> apiKeys = new ConcurrentHashMap<>();
    private final Path apiKeyPath;

    public ApiKeyService() {
        this.apiKeyPath = Paths.get("./configs/apiKeys.json");

        this.setUpStorage();
        this.loadKeysFromStorage();
    }

    private void saveToFile(final ApiKeyStorage apiKeyStorage) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final File apiKeyFile = this.apiKeyPath.toFile();
            FileUtilities.saveToFile(objectMapper, apiKeyFile, apiKeyStorage);
        } catch (final IOException e) {
            log.error("Error trying to save api keys to disk", e);
            Sentry.captureException(e);
        }
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
        final ObjectMapper objectMapper = new ObjectMapper();
        final ApiKeyStorage storage;
        try {
            storage = objectMapper.readValue(this.apiKeyPath.toFile(), ApiKeyStorage.class);
        } catch (final IOException e) {
            log.error("Error trying to read api keys from disk", e);
            Sentry.captureException(e);
            return;
        }

        for (final ApiKey apiKey : storage.getApiKeys()) {
            this.apiKeys.put(apiKey.getKey(), apiKey);
        }
        log.info("Loaded {} api keys from file", this.apiKeys.size());
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
