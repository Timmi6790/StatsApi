package de.timmi6790.mpstats.api.versions.v1.website;

import de.timmi6790.mpstats.api.versions.v1.website.models.WebsitePlayer;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Validated
@RestController
@RequestMapping("/v1/java/website/")
@Tag(name = "Java - Website")
@AllArgsConstructor
public class WebsiteController {
    private final WebsiteService websiteService;

    @GetMapping("{player}")
    public Optional<WebsitePlayer> getPlayer(@PathVariable("player") final String playerName) {
        return this.websiteService.retrievePlayerSync(playerName);
    }
}
