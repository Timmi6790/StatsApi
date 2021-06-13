package de.timmi6790.mpstats.api.versions.v1.common.filter;

import de.timmi6790.mpstats.api.security.annontations.RequireAdminPerms;
import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.board.exceptions.InvalidBoardNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.filter.models.Reason;
import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models.Filter;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.game.exceptions.InvalidGameNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.exceptions.InvalidLeaderboardCombinationRestException;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.exceptions.InvalidPlayerNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import de.timmi6790.mpstats.api.versions.v1.common.stat.exceptions.InvalidStatNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.utilities.RestUtilities;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
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

    @PostMapping("/{gameName}/{statName}/{boardName}/{playerName}")
    @Operation(summary = "Create a new filter")
    @RequireAdminPerms
    public Optional<Filter<P>> createFilter(@PathVariable final String gameName,
                                            @PathVariable final String statName,
                                            @PathVariable final String boardName,
                                            @PathVariable final String playerName,
                                            @RequestParam final Reason reason,
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                            @RequestParam final ZonedDateTime filterStart,
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                            @RequestParam final ZonedDateTime filterEnd) throws InvalidPlayerNameRestException, InvalidLeaderboardCombinationRestException, InvalidStatNameRestException, InvalidBoardNameRestException, InvalidGameNameRestException {
        RestUtilities.verifyPlayerName(this.getPlayerService(), playerName);

        final Leaderboard leaderboard = RestUtilities.getLeaderboardOrThrow(
                this.gameService,
                gameName,
                this.statService,
                statName,
                this.boardService,
                boardName,
                this.getLeaderboardService()
        );

        final Optional<P> playerOpt = this.getPlayerService().getPlayer(playerName);
        if (playerOpt.isEmpty()) {
            return Optional.empty();
        }

        final Filter<P> filter = this.filterService.addFilter(playerOpt.get(), leaderboard, reason, filterStart, filterEnd);
        return Optional.of(filter);
    }

    @PostMapping("/permanent/{gameName}/{statName}/{boardName}/{playerName}")
    @Operation(summary = "Create a new permanent filter")
    @RequireAdminPerms
    public Optional<Filter<P>> createPermanentFilter(@PathVariable final String gameName,
                                                     @PathVariable final String statName,
                                                     @PathVariable final String boardName,
                                                     @PathVariable final String playerName,
                                                     @RequestParam final Reason reason) throws InvalidPlayerNameRestException, InvalidLeaderboardCombinationRestException, InvalidStatNameRestException, InvalidBoardNameRestException, InvalidGameNameRestException {
        RestUtilities.verifyPlayerName(this.getPlayerService(), playerName);

        final Leaderboard leaderboard = RestUtilities.getLeaderboardOrThrow(
                this.gameService,
                gameName,
                this.statService,
                statName,
                this.boardService,
                boardName,
                this.getLeaderboardService()
        );

        final Optional<P> playerOpt = this.getPlayerService().getPlayer(playerName);
        if (playerOpt.isEmpty()) {
            return Optional.empty();
        }

        final Filter<P> filter = this.filterService.addPermanentFilter(playerOpt.get(), leaderboard, reason);
        return Optional.of(filter);
    }

    @DeleteMapping("/{gameName}/{statName}/{boardName}/{playerName}")
    @Operation(summary = "Remove a filter")
    @RequireAdminPerms
    public void removeFilter(@PathVariable final String gameName,
                             @PathVariable final String statName,
                             @PathVariable final String boardName,
                             @PathVariable final String playerName) {
        // TODO: Add
    }
}
