package de.timmi6790.mpstats.api.apikey;

import de.timmi6790.mpstats.api.security.annontations.RequireSuperAdminPerms;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireSuperAdminPerms
@RestController
@RequestMapping("/apiKey/")
@Tag(name = "Internal - ApiKey")
public class ApiController {
    private final ApiKeyService apiKeyService;

    @Autowired
    public ApiController(final ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @GetMapping("create")
    public void creatNewApiKey(
    ) {

    }
}
