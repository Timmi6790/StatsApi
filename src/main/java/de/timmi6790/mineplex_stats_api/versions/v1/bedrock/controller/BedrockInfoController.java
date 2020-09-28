package de.timmi6790.mineplex_stats_api.versions.v1.bedrock.controller;

import de.timmi6790.mineplex_stats_api.configs.OpenApiConfig;
import de.timmi6790.mineplex_stats_api.validator.bedrock.ValidBedrockGame;
import de.timmi6790.mineplex_stats_api.versions.v1.bedrock.model.BedrockGamesModel;
import de.timmi6790.mineplex_stats_api.versions.v1.bedrock.service.BedrockService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/bedrock/info/")
@Tag(name = OpenApiConfig.TAG_BEDROCK)
public class BedrockInfoController {
    private final BedrockService bedrockService;

    @Autowired
    public BedrockInfoController(final BedrockService bedrockService) {
        this.bedrockService = bedrockService;
    }

    @GetMapping(value = "games")
    public List<BedrockGamesModel> getGames() {
        return null;
    }

    @GetMapping(value = "saves/{game}")
    public List<Long> getSaves(
            @PathVariable("game") @ValidBedrockGame final String game
    ) {
        return null;

    }

    @GetMapping(value = "filtered/{game}")
    public List<String> getFilteredPlayer(
            @PathVariable("game") @ValidBedrockGame final String game
    ) {
        return null;

    }
}
