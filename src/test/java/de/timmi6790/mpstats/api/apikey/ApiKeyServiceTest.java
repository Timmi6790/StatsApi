package de.timmi6790.mpstats.api.apikey;

import de.timmi6790.mpstats.api.apikey.models.ApiKey;
import de.timmi6790.mpstats.api.apikey.models.ApiKeyProperties;
import de.timmi6790.mpstats.api.apikey.models.CreatedApiKey;
import de.timmi6790.mpstats.api.apikey.models.RateLimit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

class ApiKeyServiceTest {
    private ApiKeyProperties getApiKeyProperties() {
        return new ApiKeyProperties(
                new RateLimit(
                        ThreadLocalRandom.current().nextInt(10, 400),
                        ThreadLocalRandom.current().nextInt(10, 400)
                ),
                new String[]{
                        "Test",
                        "Test1",
                        "Admin"
                }
        );
    }

    @Test
    void createApiKey() {
        final ApiKeyService apiKeyService = new ApiKeyService();

        final ApiKeyProperties apiKeyProperties = this.getApiKeyProperties();

        final CreatedApiKey key = apiKeyService.createApiKey(apiKeyProperties);
        assertThat(key.getKeyInformation()).isEqualTo(apiKeyProperties);

        final Optional<ApiKey> foundApiKey = apiKeyService.getApiKey(key.getApiKey());
        assertThat(foundApiKey).isPresent();
        assertThat(foundApiKey.get().getKeyInformation()).isEqualTo(apiKeyProperties);
    }

    @Test
    void getApiKey_null() {
        final ApiKeyService apiKeyService = new ApiKeyService();
        final Optional<ApiKey> notFoundKey = apiKeyService.getApiKey(null);
        assertThat(notFoundKey).isNotPresent();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "sadsasadsaddsaadsadsadsadsdas",
            "sasdadas.ssadsaddsadsadasads",
            "saasdadsdsadsa.asddsasda.sadsdasda"
    })
    void getApiKey_invalid(final String apiKey) {
        final ApiKeyService apiKeyService = new ApiKeyService();
        final Optional<ApiKey> notFoundKey = apiKeyService.getApiKey(apiKey);
        assertThat(notFoundKey).isNotPresent();
    }

    @Test
    void getApiKey_only_public_part() {
        final ApiKeyService apiKeyService = new ApiKeyService();
        final CreatedApiKey createdKey = apiKeyService.createApiKey(this.getApiKeyProperties());

        final ApiKey savedKey = apiKeyService.getApiKey(createdKey.getApiKey()).orElseThrow(RuntimeException::new);

        // Make sure that we can't just get it based off the public part
        final Optional<ApiKey> notFoundKey = apiKeyService.getApiKey(savedKey.getPublicPart());
        assertThat(notFoundKey).isNotPresent();

        // We need to add the .d to bypass the regex
        final Optional<ApiKey> notFoundKeyRegex = apiKeyService.getApiKey(savedKey.getPublicPart() + ".d");
        assertThat(notFoundKeyRegex).isNotPresent();
    }

    @Test
    void getApiKey_public_and_hashed_part() {
        final ApiKeyService apiKeyService = new ApiKeyService();
        final CreatedApiKey createdKey = apiKeyService.createApiKey(this.getApiKeyProperties());

        final ApiKey savedKey = apiKeyService.getApiKey(createdKey.getApiKey()).orElseThrow(RuntimeException::new);
        final Optional<ApiKey> notFoundKey = apiKeyService.getApiKey(savedKey.getPublicPart() + "." + savedKey.getHashedPrivatePart());
        assertThat(notFoundKey).isNotPresent();
    }

    @Test
    void initialize_load_saved_keys() {
        final ApiKeyService apiKeyService = new ApiKeyService();

        final ApiKeyProperties apiKeyProperties = this.getApiKeyProperties();

        final CreatedApiKey createdKey = apiKeyService.createApiKey(apiKeyProperties);

        final ApiKeyService newApiKeyService = new ApiKeyService();
        final Optional<ApiKey> foundApiKey = newApiKeyService.getApiKey(createdKey.getApiKey());
        assertThat(foundApiKey).isPresent();
        assertThat(foundApiKey.get().getKeyInformation()).isEqualTo(apiKeyProperties);
    }
}