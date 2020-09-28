package de.timmi6790.mineplex_stats_api.versions.v1.java.controller;

import de.timmi6790.mineplex_stats_api.configs.OpenApiConfig;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/v1/java/group/")
@Tag(name = OpenApiConfig.TAG_JAVA)
public class JavaGroupController {
    @GetMapping(value = "groups")
    public void getGroups() {

    }

    @GetMapping(value = "{group}/{player}/{stat}/{board}")
    public void getPlayerStats(
            @PathVariable("group") final String group,
            @PathVariable("stat") final String stat,
            @PathVariable("board") final String board,
            @RequestParam(required = false, defaultValue = "#{T(java.time.LocalDateTime).now()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateTime,
            @RequestParam(required = false, defaultValue = "true") final boolean filter,
            @RequestParam("playerUUID") final Optional<UUID> playerUUIDOpt
    ) {
        // Include lb, website and generated stats
    }
}
