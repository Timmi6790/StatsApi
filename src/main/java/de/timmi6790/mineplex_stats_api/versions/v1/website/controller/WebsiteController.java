package de.timmi6790.mineplex_stats_api.versions.v1.website.controller;

import de.timmi6790.mineplex_stats_api.configs.OpenApiConfig;
import de.timmi6790.mineplex_stats_api.validator.java.ValidJavaPlayerName;
import de.timmi6790.mineplex_stats_api.versions.v1.website.service.WebsiteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/v1/website/")
@Tag(name = OpenApiConfig.TAG_WEBSITE)
public class WebsiteController {
    private final WebsiteService websiteService;

    @Autowired
    public WebsiteController(final WebsiteService websiteService) {
        this.websiteService = websiteService;
    }

    @GetMapping(value = "{player}")
    public void getPlayer(
            @PathVariable("player") @ValidJavaPlayerName final String player
    ) {

    }

    @GetMapping(value = "{player}/stats")
    public void getPlayerStats(
            @PathVariable("player") @ValidJavaPlayerName final String player
    ) {

    }

    @GetMapping(value = "{player}/stats/{game}")
    public void getPlayerStatsGame(
            @PathVariable("player") @ValidJavaPlayerName final String player,
            @PathVariable("game") final String game
    ) {

    }
}
