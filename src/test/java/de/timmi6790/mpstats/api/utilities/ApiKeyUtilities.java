package de.timmi6790.mpstats.api.utilities;

import de.timmi6790.mpstats.api.apikey.ApiKeyService;
import de.timmi6790.mpstats.api.apikey.models.ApiKey;
import de.timmi6790.mpstats.api.apikey.models.RateLimit;

import java.util.UUID;

public class ApiKeyUtilities {
    private static final ApiKey SUPER_ADMIN = generateKey("SUPERADMIN");
    private static final ApiKey ADMIN = generateKey("ADMIN");
    private static final ApiKey USER = generateKey();

    private static ApiKey generateKey(final String... authorities) {
        return new ApiKey(
                UUID.randomUUID(),
                new RateLimit(Integer.MAX_VALUE, Integer.MAX_VALUE),
                authorities
        );
    }

    public static UUID getSuperAdminApiKey(final ApiKeyService apiKeyService) {
        apiKeyService.addApiKey(SUPER_ADMIN);
        return SUPER_ADMIN.getKey();
    }

    public static UUID getAdminApiKey(final ApiKeyService apiKeyService) {
        apiKeyService.addApiKey(ADMIN);
        return ADMIN.getKey();
    }

    public static UUID getUserApiKey(final ApiKeyService apiKeyService) {
        apiKeyService.addApiKey(USER);
        return USER.getKey();
    }
}
