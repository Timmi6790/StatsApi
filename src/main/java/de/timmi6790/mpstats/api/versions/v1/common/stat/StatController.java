package de.timmi6790.mpstats.api.versions.v1.common.stat;

import de.timmi6790.mpstats.api.security.annontations.RequireAdminPerms;
import de.timmi6790.mpstats.api.versions.v1.common.stat.exceptions.InvalidStatNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import de.timmi6790.mpstats.api.versions.v1.common.utilities.RestUtilities;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Getter(AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class StatController {
    private final StatService statService;

    @GetMapping
    @Operation(summary = "Find all available stats")
    public List<Stat> getStats() {
        return this.statService.getStats();
    }

    @GetMapping("/{statName}")
    @Operation(summary = "Find stat by name")
    public Stat getStat(@PathVariable final String statName) throws InvalidStatNameRestException {
        return RestUtilities.getStatOrThrow(this.statService, statName);
    }

    @PutMapping("/{statName}")
    @Operation(summary = "Create a new stat")
    @RequireAdminPerms
    public Stat createStat(@PathVariable final String statName,
                           @RequestParam final String websiteName,
                           @RequestParam final String cleanName,
                           @RequestParam final boolean isAchievement) {
        return this.statService.getStatOrCreate(websiteName, statName, cleanName, isAchievement);
    }
}
