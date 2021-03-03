package de.timmi6790.mpstats.api.versions.v1.bedrock.info;

import de.timmi6790.mpstats.api.configs.OpenApiConfig;
import de.timmi6790.mpstats.api.versions.v1.bedrock.info.models.BedrockGamesModel;
import de.timmi6790.mpstats.api.versions.v1.bedrock.validators.ValidBedrockGame;
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

    @Autowired
    public BedrockInfoController() {

    }

    @GetMapping(value = "games")
    public List<BedrockGamesModel> getGames() {
        return null;
    }

    @GetMapping(value = "saves/{game}")
    public List<Long> getSaves(
            @PathVariable @ValidBedrockGame final String game
    ) {
        return null;

    }

    @GetMapping(value = "filtered/{game}")
    public List<String> getFilteredPlayer(
            @PathVariable @ValidBedrockGame final String game
    ) {
        return null;

    }
}
