package de.timmi6790.mpstats.api.versions.v1.java.player_stats;

import de.timmi6790.mpstats.api.configs.OpenApiConfig;
import de.timmi6790.mpstats.api.versions.v1.java.player_stats.models.PlayerStatsModel;
import de.timmi6790.mpstats.api.versions.v1.java.player_stats.models.PlayerStatsRatioModel;
import de.timmi6790.mpstats.api.versions.v1.java.validators.ValidJavaPlayerName;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/v1/java/player/")
@Tag(name = OpenApiConfig.TAG_JAVA)
public class JavaPlayerStatsController {
    private final JavaPlayerStatsService javaPlayerStatsService;

    @Autowired
    public JavaPlayerStatsController(final JavaPlayerStatsService javaPlayerStatsService) {
        this.javaPlayerStatsService = javaPlayerStatsService;
    }

    @SneakyThrows
    @Operation(summary = "Get player stats")
    @GetMapping(value = "{player}/{game}/{board}")
    public PlayerStatsModel getPlayerStats(
            @PathVariable @ValidJavaPlayerName final String player,
            @PathVariable final String game,
            @PathVariable final String board,
            @RequestParam(required = false, defaultValue = "#{T(java.time.LocalDateTime).now()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateTime,
            @RequestParam(required = false, defaultValue = "true") final boolean filter,
            @RequestParam(required = false, defaultValue = "null") final UUID playerUUID
    ) {
        return null;
    }

    @SneakyThrows
    @GetMapping(value = "{player}/ratio/{stat}/{board}")
    public PlayerStatsRatioModel getPlayerStatRatio(
            @PathVariable @ValidJavaPlayerName final String player,
            @PathVariable final String stat,
            @PathVariable final String board,
            @RequestParam(required = false, defaultValue = "#{T(java.time.LocalDateTime).now()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateTime,
            @RequestParam(required = false, defaultValue = "true") final boolean filter,
            @RequestParam(required = false, defaultValue = "null") final UUID playerUUID
    ) {
        return null;
    }
}
