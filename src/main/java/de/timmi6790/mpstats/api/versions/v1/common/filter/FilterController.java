package de.timmi6790.mpstats.api.versions.v1.common.filter;

import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models.Filter;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.RepositoryPlayer;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Getter(AccessLevel.PROTECTED)
public class FilterController<P extends Player & RepositoryPlayer, S extends PlayerService<P>> {
    private final FilterService<P, S> filterService;

    protected LeaderboardService getLeaderboardService() {
        return this.filterService.getLeaderboardService();
    }

    protected S getPlayerService() {
        return this.filterService.getPlayerService();
    }

    @GetMapping
    @Operation(summary = "Find all available filters")
    public List<Filter<P>> getFilters() {
        return this.filterService.getFilters();
    }

    @PostMapping("/{gameName}/{statName}/{boardName}/{playerName}")
    @Operation(summary = "Create a new filter")
    public Optional<Filter<P>> createFilter(@PathVariable final String gameName,
                                            @PathVariable final String statName,
                                            @PathVariable final String boardName,
                                            @PathVariable final String playerName,
                                            @RequestParam final Reason reason,
                                            @RequestParam final LocalDateTime filterStart,
                                            @RequestParam final LocalDateTime filterEnd) {
        // TODO: Add spring security

        final Optional<Leaderboard> leaderboardOpt = this.getLeaderboardService().getLeaderboard(gameName, statName, boardName);
        if (leaderboardOpt.isEmpty()) {
            return Optional.empty();
        }

        final Optional<P> playerOpt = this.getPlayerService().getPlayer(playerName);
        if (playerOpt.isEmpty()) {
            return Optional.empty();
        }

        final Filter<P> filter = this.filterService.addFilter(playerOpt.get(), leaderboardOpt.get(), reason, filterStart, filterEnd);
        return Optional.of(filter);
    }

    @DeleteMapping("/{gameName}/{statName}/{boardName}/{playerName}")
    @Operation(summary = "Remove a filter")
    public void removeFilter(@PathVariable final String gameName,
                             @PathVariable final String statName,
                             @PathVariable final String boardName,
                             @PathVariable final String playerName) {
        // TODO: Add
    }
}
