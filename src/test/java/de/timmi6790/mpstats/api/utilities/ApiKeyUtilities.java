package de.timmi6790.mpstats.api.utilities;

import de.timmi6790.mpstats.api.apikey.ApiKeyService;
import de.timmi6790.mpstats.api.apikey.models.ApiKeyProperties;
import de.timmi6790.mpstats.api.apikey.models.CreatedApiKey;
import de.timmi6790.mpstats.api.apikey.models.RateLimit;

public class ApiKeyUtilities {
    private static final ApiKeyProperties SUPER_ADMIN = generateApiKeyInfo("SUPERADMIN");
    private static final ApiKeyProperties ADMIN = generateApiKeyInfo("ADMIN");
    private static final ApiKeyProperties USER = generateApiKeyInfo();

    private static ApiKeyProperties generateApiKeyInfo(final String... authorities) {
        return new ApiKeyProperties(
                new RateLimit(Integer.MAX_VALUE, Integer.MAX_VALUE),
                authorities
        );
    }

    private static String generateApiKey(final ApiKeyService apiKeyService, final ApiKeyProperties apiKeyProperties) {
        final CreatedApiKey createdApiKey = apiKeyService.createApiKey(apiKeyProperties);
        return createdApiKey.getApiKey();
    }

    public static String getSuperAdminApiKey(final ApiKeyService apiKeyService) {
        return generateApiKey(apiKeyService, SUPER_ADMIN);
    }

    public static String getAdminApiKey(final ApiKeyService apiKeyService) {
        return generateApiKey(apiKeyService, ADMIN);
    }

    public static String getUserApiKey(final ApiKeyService apiKeyService) {
        return generateApiKey(apiKeyService, USER);
    }
}
