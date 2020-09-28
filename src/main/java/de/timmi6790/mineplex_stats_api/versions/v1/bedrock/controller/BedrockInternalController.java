package de.timmi6790.mineplex_stats_api.versions.v1.bedrock.controller;

import de.timmi6790.mineplex_stats_api.configs.OpenApiConfig;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/bedrock/internal/")
@Tag(name = OpenApiConfig.TAG_BEDROCK_INTERNAL)
public class BedrockInternalController {
    @PostMapping(value = "filter")
    public void getGames() {

    }
}
