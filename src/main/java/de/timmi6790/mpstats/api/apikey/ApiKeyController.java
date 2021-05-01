package de.timmi6790.mpstats.api.apikey;

import de.timmi6790.mpstats.api.apikey.models.ApiKey;
import de.timmi6790.mpstats.api.security.annontations.RequireSuperAdminPerms;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequireSuperAdminPerms
@RestController
@RequestMapping("/apiKey/")
@Tag(name = "Internal - ApiKey")
public class ApiKeyController {
    private final ApiKeyService apiKeyService;

    @Autowired
    public ApiKeyController(final ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @PostMapping("create")
    public ApiKey creatNewApiKey(@RequestParam final int dailyRateLimit,
                                 @RequestParam final int minuteRateLimit) {
        final ApiKey apiKey = ApiKey.of(dailyRateLimit, minuteRateLimit);
        this.apiKeyService.addApiKey(apiKey);
        return apiKey;
    }
}
