package de.timmi6790.mineplex_stats_api.versions.v1.java.controller;

import de.timmi6790.mineplex_stats_api.configs.OpenApiConfig;
import de.timmi6790.mineplex_stats_api.validator.java.ValidJavaPlayerName;
import de.timmi6790.mineplex_stats_api.versions.v1.java.model.PlayerStatsModel;
import de.timmi6790.mineplex_stats_api.versions.v1.java.model.PlayerStatsRatioModel;
import de.timmi6790.mineplex_stats_api.versions.v1.java.service.JavaPlayerService;
import de.timmi6790.mineplex_stats_api.versions.v1.website.model.WebsitePlayerModel;
import de.timmi6790.mineplex_stats_api.versions.v1.website.service.WebsiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Validated
@RestController
@RequestMapping("/v1/java/player/")
@Tag(name = OpenApiConfig.TAG_JAVA)
public class JavaPlayerController {
    private final WebsiteService websiteService;
    private final JavaPlayerService javaPlayerService;

    @Autowired
    public JavaPlayerController(final WebsiteService websiteService, final JavaPlayerService javaPlayerService) {
        this.websiteService = websiteService;
        this.javaPlayerService = javaPlayerService;
    }

    @SneakyThrows
    @Operation(summary = "Get player stats")
    @GetMapping(value = "{player}/{game}/{board}")
    public PlayerStatsModel getPlayerStats(
            @PathVariable("player") @ValidJavaPlayerName final String player,
            @PathVariable("game") final String game,
            @PathVariable("board") final String board,
            @RequestParam(required = false, defaultValue = "#{T(java.time.LocalDateTime).now()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateTime,
            @RequestParam(value = "filter", required = false, defaultValue = "true") final boolean filter,
            @RequestParam("playerUUID") final Optional<UUID> playerUUIDOpt
    ) {
        return null;
    }

    @SneakyThrows
    @GetMapping(value = "{player}/ratio/{stat}/{board}")
    public PlayerStatsRatioModel getPlayerStatRatio(
            @PathVariable("player") @ValidJavaPlayerName final String player,
            @PathVariable("stat") final String stat,
            @PathVariable("board") final String board,
            @RequestParam(required = false, defaultValue = "#{T(java.time.LocalDateTime).now()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateTime,
            @RequestParam(value = "filter", required = false, defaultValue = "true") final boolean filter,
            @RequestParam("playerUUID") final Optional<UUID> playerUUIDOpt
    ) {
        final CompletableFuture<Optional<WebsitePlayerModel>> webStatsFuture = this.websiteService.getPlayerStats(player);

        final UUID playerUUID = playerUUIDOpt.orElse(null);
        final String boardName = board;
        final String playerName = player;

        return null;
    }
}
