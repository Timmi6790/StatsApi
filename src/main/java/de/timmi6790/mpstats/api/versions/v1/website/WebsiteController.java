package de.timmi6790.mpstats.api.versions.v1.website;

import de.timmi6790.mpstats.api.versions.v1.exceptions.InvalidGameException;
import de.timmi6790.mpstats.api.versions.v1.exceptions.InvalidPlayerNameException;
import de.timmi6790.mpstats.api.versions.v1.java.validators.ValidJavaPlayerName;
import de.timmi6790.mpstats.api.versions.v1.website.models.WebsitePlayerModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@Validated
@RestController
@RequestMapping("/v1/website/")
@Tag(name = "Website")
public class WebsiteController {
    private final WebsiteService websiteService;

    @Autowired
    public WebsiteController(final WebsiteService websiteService) {
        this.websiteService = websiteService;
    }

    private WebsitePlayerModel getWebsitePlayerModel(final String playerName) {
        final Optional<WebsitePlayerModel> playerData = this.websiteService.retrievePlayerSync(playerName);
        if (playerData.isPresent()) {
            return playerData.get();
        }

        throw new InvalidPlayerNameException(playerName);
    }

    @GetMapping(value = "{player}")
    public WebsitePlayerModel getPlayer(
            @PathVariable("player") @ValidJavaPlayerName final String player
    ) {
        return this.getWebsitePlayerModel(player);
    }

    @GetMapping(value = "{player}/stats")
    public Map<String, Map<String, Long>> getPlayerStats(
            @PathVariable("player") @ValidJavaPlayerName final String player
    ) {
        return this.getWebsitePlayerModel(player).stats();
    }

    @GetMapping(value = "{player}/stats/{game}")
    public Map<String, Long> getPlayerStatsGame(
            @PathVariable("player") @ValidJavaPlayerName final String player,
            @PathVariable("game") final String game
    ) {
        final Map<String, Map<String, Long>> stats = this.getWebsitePlayerModel(player).stats();
        final Map<String, Long> gameStats = stats.get(game);
        if (gameStats != null) {
            return gameStats;
        }

        throw new InvalidGameException();
    }
}
