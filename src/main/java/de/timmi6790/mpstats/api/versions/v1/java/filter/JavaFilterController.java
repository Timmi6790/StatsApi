package de.timmi6790.mpstats.api.versions.v1.java.filter;

import de.timmi6790.mpstats.api.security.annontations.RequireAdminPerms;
import de.timmi6790.mpstats.api.versions.v1.common.filter.FilterController;
import de.timmi6790.mpstats.api.versions.v1.common.filter.Reason;
import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models.Filter;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.java.player.JavaPlayerService;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/v1/java/filter")
@Tag(name = "Java - Filter")
public class JavaFilterController extends FilterController<JavaPlayer, JavaPlayerService> {
    @Autowired
    public JavaFilterController(final JavaFilterService filterService) {
        super(filterService);
    }

    @PostMapping("/{gameName}/{statName}/{boardName}/{playerUUID}")
    @Operation(summary = "Create a new filter")
    @RequireAdminPerms
    public Optional<Filter<JavaPlayer>> createFilter(@PathVariable final String gameName,
                                                     @PathVariable final String statName,
                                                     @PathVariable final String boardName,
                                                     @PathVariable final UUID playerUUID,
                                                     @RequestParam final Reason reason,
                                                     @RequestParam final LocalDateTime filterStart,
                                                     @RequestParam final LocalDateTime filterEnd) {
        final Optional<Leaderboard> leaderboardOpt = this.getLeaderboardService().getLeaderboard(gameName, statName, boardName);
        if (leaderboardOpt.isEmpty()) {
            return Optional.empty();
        }

        final Optional<JavaPlayer> playerOpt = this.getPlayerService().getPlayer(playerUUID);
        if (playerOpt.isEmpty()) {
            return Optional.empty();
        }

        final Filter<JavaPlayer> filter = this.getFilterService().addFilter(
                playerOpt.get(),
                leaderboardOpt.get(),
                reason,
                filterStart,
                filterEnd
        );
        return Optional.of(filter);
    }
}
