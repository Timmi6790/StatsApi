package de.timmi6790.mineplex_stats_api.versions.v1.java.internal;

import de.timmi6790.mineplex_stats_api.configs.OpenApiConfig;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/java/internal/")
@Tag(name = OpenApiConfig.TAG_JAVA_INTERNAL)
public class JavaInternalController {
    @PostMapping(value = "filter")
    public void getFilter() {

    }

    @PostMapping(value = "alias/game")
    public void addAliasGame() {

    }

    @PostMapping(value = "alias/stat")
    public void addAliasStat() {

    }

    @PostMapping(value = "alias/board")
    public void addAliasBoard() {

    }

    @PostMapping(value = "alias/group")
    public void addAliasGroup() {

    }

    @PostMapping(value = "games/game")
    public void addGame() {

    }

    @PostMapping(value = "stats/stat")
    public void addStat() {

    }

    @PostMapping(value = "boards/board")
    public void addBoard() {

    }

    @PostMapping(value = "leaderboards/leaderboard")
    public void addLeaderboard() {

    }
}
