package de.timmi6790.mpstats.api.versions.v1.common.stat;

import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

public abstract class StatController {
    @Getter(value = AccessLevel.PROTECTED)
    private final StatService statService;

    protected StatController(final StatService statService) {
        this.statService = statService;
    }

    @GetMapping
    @Operation(summary = "Find all available stats")
    public List<Stat> getStats() {
        return this.statService.getStats();
    }

    @GetMapping(value = "/{statName}")
    @Operation(summary = "Find stat by name")
    public Optional<Stat> getStat(@PathVariable final String statName) {
        return this.statService.getStat(statName);
    }

    @PostMapping(value = "/{statName}")
    @Operation(summary = "Create a new stat")
    public Stat createStat(@PathVariable final String statName,
                           @RequestParam final String websiteName,
                           @RequestParam final String cleanName,
                           @RequestParam final boolean isAchievement) {
        // TODO: Add spring security
        return this.statService.getStatOrCreate(websiteName, statName, cleanName, isAchievement);
    }
}
