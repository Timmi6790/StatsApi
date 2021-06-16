package de.timmi6790.mpstats.api.apikey;

import de.timmi6790.mpstats.api.apikey.models.ApiKeyProperties;
import de.timmi6790.mpstats.api.apikey.models.CreatedApiKey;
import de.timmi6790.mpstats.api.apikey.models.RateLimit;
import de.timmi6790.mpstats.api.security.annontations.RequireSuperAdminPerms;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequireSuperAdminPerms
@RestController
@AllArgsConstructor
@RequestMapping("/apiKey/")
@Tag(name = "Internal - ApiKey")
public class ApiKeyController {
    private final ApiKeyService apiKeyService;

    @PostMapping("create")
    public CreatedApiKey creatNewApiKey(@RequestParam final int dailyRateLimit,
                                        @RequestParam final int minuteRateLimit) {
        return this.apiKeyService.createApiKey(
                new ApiKeyProperties(
                        new RateLimit(dailyRateLimit, minuteRateLimit),
                        new String[0]
                )
        );
    }
}
