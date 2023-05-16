package de.timmi6790.mpstats.api.apikey;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Log4j2
@Service
public class ApiKeyService {
    private final Environment env;
    private final Set<String> apiKeys = new HashSet<>();

    public ApiKeyService(Environment env) {
        this.env = env;

        this.loadKeysFromEnvironment();
    }

    private void loadKeysFromEnvironment() {
        final String keys = env.getProperty("API_KEYS");
        if (keys == null || keys.isEmpty()) {
            return;
        }

        final String[] splitKeys = keys.split(",");
        this.apiKeys.addAll(Arrays.asList(splitKeys));
        log.info("Loaded {} api keys from environment", this.apiKeys.size());
    }

    public boolean isValidApiKey(final String apiKey) {
        return this.apiKeys.contains(apiKey);
    }
}
