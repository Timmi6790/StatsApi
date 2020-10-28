package de.timmi6790.mineplex_stats_api.versions.v1.java.controller;

import de.timmi6790.mineplex_stats_api.configs.OpenApiConfig;
import de.timmi6790.mineplex_stats_api.versions.v1.java.repository.models.GroupsModel;
import de.timmi6790.mineplex_stats_api.versions.v1.java.service.JavaPlayerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/java/group/")
@Tag(name = OpenApiConfig.TAG_JAVA)
public class JavaGroupController {
    private final JavaPlayerService javaPlayerService;

    @Autowired
    public JavaGroupController(final JavaPlayerService javaPlayerService) {
        this.javaPlayerService = javaPlayerService;
    }

    @GetMapping(value = "groups")
    public List<GroupsModel> getGroups() {
        return this.javaPlayerService.getGroups();
    }

    @GetMapping(value = "{group}/{player}/{stat}/{board}")
    public void getPlayerStats(
            @PathVariable final String group,
            @PathVariable final String stat,
            @PathVariable final String board,
            @RequestParam(required = false, defaultValue = "#{T(java.time.LocalDateTime).now()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateTime,
            @RequestParam(required = false, defaultValue = "true") final boolean filter,
            @RequestParam(required = false, defaultValue = "null") final UUID playerUUID
    ) {
        // Include lb, website and generated stats
    }
}
