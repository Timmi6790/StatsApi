package de.timmi6790.mpstats.api.versions.v1.java.filter;

import de.timmi6790.mpstats.api.versions.v1.common.filter.FilterController;
import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models.Filter;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.java.player.JavaPlayerService;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaRepositoryPlayer;
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
public class JavaFilterController extends FilterController<JavaRepositoryPlayer, JavaPlayerService> {
    @Autowired
    public JavaFilterController(final JavaFilterService filterService) {
        super(filterService);
    }

    @PostMapping("/{gameName}/{statName}/{boardName}/{playerUUID}")
    @Operation(summary = "Create a new filter")
    public Optional<Filter<JavaRepositoryPlayer>> createFilter(@PathVariable final String gameName,
                                                               @PathVariable final String statName,
                                                               @PathVariable final String boardName,
                                                               @PathVariable final UUID playerUUID,
                                                               @RequestParam final String reason,
                                                               @RequestParam final LocalDateTime filterStart,
                                                               @RequestParam final LocalDateTime filterEnd) {
        // TODO: Add spring security

        final Optional<Leaderboard> leaderboardOpt = this.getLeaderboardService().getLeaderboard(gameName, statName, boardName);
        if (leaderboardOpt.isEmpty()) {
            return Optional.empty();
        }

        final Optional<JavaRepositoryPlayer> playerOpt = this.getPlayerService().getPlayer(playerUUID);
        if (playerOpt.isEmpty()) {
            return Optional.empty();
        }

        final Filter<JavaRepositoryPlayer> filter = this.getFilterService().addFilter(
                playerOpt.get(),
                leaderboardOpt.get(),
                reason,
                filterStart,
                filterEnd
        );
        return Optional.of(filter);
    }
}
