package de.timmi6790.mineplex_stats_api.versions.v1.bedrock.controller;

import de.timmi6790.mineplex_stats_api.configs.OpenApiConfig;
import de.timmi6790.mineplex_stats_api.validator.bedrock.ValidBedrockGame;
import de.timmi6790.mineplex_stats_api.versions.v1.bedrock.model.BedrockLeaderboardModel;
import de.timmi6790.mineplex_stats_api.versions.v1.bedrock.service.BedrockService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/bedrock/leaderboard/")
@Tag(name = OpenApiConfig.TAG_BEDROCK)
public class BedrockLeaderboardController {
    private final BedrockService bedrockService;

    @Autowired
    public BedrockLeaderboardController(final BedrockService bedrockService) {
        this.bedrockService = bedrockService;
    }

    @GetMapping(value = "{game}")
    public BedrockLeaderboardModel getLeaderboard(
            @PathVariable("game") @ValidBedrockGame final String game,
            @RequestParam(required = false, defaultValue = "1") @Min(1) @Max(100) final int startPosition,
            @RequestParam(required = false, defaultValue = "100") @Min(1) @Max(100) final int endPosition,
            @RequestParam(required = false, defaultValue = "true") final boolean filter,
            @RequestParam(required = false, defaultValue = "#{T(java.time.LocalDateTime).now()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateTime
    ) {
        return null;
    }
}
