package de.timmi6790.mineplex_stats_api.versions.v1.java.controller;

import de.timmi6790.mineplex_stats_api.configs.OpenApiConfig;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/java/leaderboard/")
@Tag(name = OpenApiConfig.TAG_JAVA)
public class JavaLeaderboardController {
    @GetMapping(value = "{game}/{stat}/{board}")
    public void getLeaderboard(
            @PathVariable("game") final String game,
            @PathVariable("stat") final String stat,
            @PathVariable("board") final String board,
            @RequestParam(required = false, defaultValue = "1") @Min(1) @Max(1_000) final int startPosition,
            @RequestParam(required = false, defaultValue = "1000") @Min(1) @Max(1_000) final int endPosition,
            @RequestParam(required = false, defaultValue = "true") final boolean filter,
            @RequestParam(required = false, defaultValue = "#{T(java.time.LocalDateTime).now()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateTime
    ) {

    }
}
