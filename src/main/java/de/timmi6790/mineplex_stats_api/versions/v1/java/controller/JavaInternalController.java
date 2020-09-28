package de.timmi6790.mineplex_stats_api.versions.v1.java.controller;

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
    public void getGames() {

    }

    @PostMapping(value = "alias")
    public void addAliasGame() {

    }
}
