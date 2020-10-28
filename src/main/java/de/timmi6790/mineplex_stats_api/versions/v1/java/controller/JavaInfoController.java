package de.timmi6790.mineplex_stats_api.versions.v1.java.controller;

import de.timmi6790.mineplex_stats_api.configs.OpenApiConfig;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/java/info/")
@Tag(name = OpenApiConfig.TAG_JAVA)
public class JavaInfoController {
    @GetMapping(value = "games")
    public void getGames() {

    }

    @GetMapping(value = "saves/{game}/{stat}/{board}")
    public void getSaves(
            @PathVariable final String game,
            @PathVariable final String stat,
            @PathVariable final String board
    ) {

    }

    @GetMapping(value = "filtered/{game}/{stat}/{board}")
    public void getFilteredPlayer(
            @PathVariable final String game,
            @PathVariable final String stat,
            @PathVariable final String board
    ) {

    }
}
