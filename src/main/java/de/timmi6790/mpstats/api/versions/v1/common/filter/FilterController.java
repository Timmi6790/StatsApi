package de.timmi6790.mpstats.api.versions.v1.common.filter;

import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.filter.models.Reason;
import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models.Filter;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PROTECTED)
// TODO: Add tests
public class FilterController<P extends Player, S extends PlayerService<P>> {
    private final GameService gameService;
    private final StatService statService;
    private final BoardService boardService;
    private final FilterService<P, S> filterService;

    protected LeaderboardService getLeaderboardService() {
        return this.filterService.getLeaderboardService();
    }

    protected S getPlayerService() {
        return this.filterService.getPlayerService();
    }

    @GetMapping
    @Operation(summary = "Find all available filters")
    // TODO: Look into pageable
    /*
        public List<Filter<P>> getFilters(@ParameterObject @PageableDefault(page = 0, size = 20) final Pageable pageable) {
        // TODO: Look into pageable

        final List<Filter<P>> filters = this.filterService.getFilters();

        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), filters.size());

        return new PageImpl<>(filters.subList(start, end), pageable, filters.size());
    }
     */
    public List<Filter<P>> getFilters() {
        return this.filterService.getFilters();
    }

    @GetMapping("/reasons")
    @Operation(summary = "Find all available filter reasons")
    public Set<Reason> getFilterReasons() {
        return EnumSet.allOf(Reason.class);
    }
}
