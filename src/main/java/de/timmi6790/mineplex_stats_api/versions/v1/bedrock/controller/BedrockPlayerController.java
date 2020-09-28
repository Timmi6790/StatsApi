package de.timmi6790.mineplex_stats_api.versions.v1.bedrock.controller;

import de.timmi6790.mineplex_stats_api.configs.OpenApiConfig;
import de.timmi6790.mineplex_stats_api.validator.bedrock.ValidBedrockPlayerName;
import de.timmi6790.mineplex_stats_api.versions.v1.bedrock.model.BedrockPlayerStatsModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/bedrock/player/")
@Tag(name = OpenApiConfig.TAG_BEDROCK)
public class BedrockPlayerController {
    @GetMapping(value = "{player}")
    public BedrockPlayerStatsModel getSaves(
            @PathVariable("player") @ValidBedrockPlayerName final String player,
            @RequestParam(required = false, defaultValue = "#{T(java.time.LocalDateTime).now()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateTime,
            @RequestParam(required = false, defaultValue = "true") final boolean filter
    ) {
        return null;
    }
}
