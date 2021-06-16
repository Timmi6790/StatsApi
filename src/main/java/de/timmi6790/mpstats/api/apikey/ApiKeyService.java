package de.timmi6790.mpstats.api.apikey;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.timmi6790.mpstats.api.apikey.models.ApiKey;
import de.timmi6790.mpstats.api.apikey.models.ApiKeyProperties;
import de.timmi6790.mpstats.api.apikey.models.CreatedApiKey;
import de.timmi6790.mpstats.api.apikey.models.RateLimit;
import de.timmi6790.mpstats.api.utilities.FileUtilities;
import io.sentry.Sentry;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Service
public class ApiKeyService {
    private static final int PRIVATE_PART_MIN = 80;
    private static final int PRIVATE_PART_MAX = 100;
    private static final Pattern API_KEY_PATTERN = Pattern.compile("^([^.]+)\\.([^.]+)$");

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final Map<String, ApiKey> apiKeys = new ConcurrentHashMap<>();
    private final Path apiKeyPath;

    public ApiKeyService() {
        this.apiKeyPath = Paths.get("./configs/apiKeys.json");

        this.setUpStorage();
        this.loadKeysFromStorage();
    }

    private void saveApiKeysToFile() {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final File apiKeyFile = this.apiKeyPath.toFile();

            FileUtilities.saveToFile(objectMapper, apiKeyFile, this.apiKeys.values());
        } catch (final IOException e) {
            log.error("Error trying to save api keys to disk", e);
            Sentry.captureException(e);
        }
    }

    private void setUpStorage() {
        if (!this.apiKeyPath.toFile().exists()) {
            // Make sure that the file exists
            this.saveApiKeysToFile();

            final CreatedApiKey apiCreated = this.createApiKey(
                    new ApiKeyProperties(
                            new RateLimit(
                                    Integer.MAX_VALUE,
                                    Integer.MAX_VALUE
                            ),
                            new String[]{"SUPERADMIN"}
                    )
            );
            log.info(
                    """
                                                        
                            -------------------------------------------------

                            Important:
                            Created new master api key: {}


                            -------------------------------------------------""",
                    apiCreated.getApiKey()
            );
        }
    }

    private void loadKeysFromStorage() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final List<ApiKey> savedApiKeys;
        try {
            savedApiKeys = objectMapper.readValue(
                    this.apiKeyPath.toFile(),
                    new TypeReference<>() {
                    }
            );
        } catch (final IOException e) {
            log.error("Error trying to read api keys from disk", e);
            Sentry.captureException(e);
            return;
        }

        for (final ApiKey key : savedApiKeys) {
            this.apiKeys.put(key.getPublicPart(), key);
        }
        log.info("Loaded {} api keys from file", this.apiKeys.size());
    }

    public CreatedApiKey createApiKey(final ApiKeyProperties keyInfo) {
        // We use uuids for the public part to prevent duplicate values
        final String publicPart = UUID.randomUUID().toString().replace("-", "");
        final String privatePart = RandomStringUtils.random(
                ThreadLocalRandom.current().nextInt(PRIVATE_PART_MIN, PRIVATE_PART_MAX),
                0,
                0,
                true,
                true,
                null,
                new SecureRandom()
        );

        log.info("Created new api key");
        final String hashedPrivatePart = this.passwordEncoder.encode(privatePart);
        this.apiKeys.put(
                publicPart,
                new ApiKey(
                        publicPart,
                        hashedPrivatePart,
                        keyInfo
                )
        );
        this.saveApiKeysToFile();

        final String apiKey = publicPart + "." + privatePart;
        return new CreatedApiKey(
                apiKey,
                keyInfo
        );
    }

    public Optional<ApiKey> getApiKey(final String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            return Optional.empty();
        }

        // Each apikey has an public and an private part divided by .
        // The public part is not encoded and use to quickly identify the apikey
        final Matcher apiKeyParts = API_KEY_PATTERN.matcher(apiKey);
        if (!apiKeyParts.find()) {
            return Optional.empty();
        }

        final String publicPart = apiKeyParts.group(1);
        final ApiKey storedKey = this.apiKeys.get(publicPart);
        if (storedKey == null) {
            return Optional.empty();
        }

        final String privatePart = apiKeyParts.group(2);
        if (this.passwordEncoder.matches(privatePart, storedKey.getHashedPrivatePart())) {
            return Optional.of(storedKey);
        }

        return Optional.empty();
    }
}
