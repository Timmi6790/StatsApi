package de.timmi6790.mpstats.api.versions.v1.common.filter;

import de.timmi6790.mpstats.api.utilities.LeaderboardUtilities;
import de.timmi6790.mpstats.api.utilities.PlayerUtilities;
import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.filter.models.Reason;
import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models.Filter;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: Add more specific tests
public abstract class AbstractFilterServiceTest<P extends Player, S extends PlayerService<P>, F extends FilterService<P, S>> {
    protected final Supplier<F> filterServiceSupplier;
    protected final F filterService;
    protected final GameService gameService;
    protected final StatService statService;
    protected final BoardService boardService;

    protected AbstractFilterServiceTest(final Supplier<F> filterServiceSupplier,
                                        final GameService gameService,
                                        final StatService statService,
                                        final BoardService boardService) {
        this.filterServiceSupplier = filterServiceSupplier;
        this.filterService = filterServiceSupplier.get();
        this.gameService = gameService;
        this.statService = statService;
        this.boardService = boardService;
    }

    protected S getPlayerService() {
        return this.filterService.getPlayerService();
    }

    protected LeaderboardService getLeaderboardService() {
        return this.filterService.getLeaderboardService();
    }

    protected P generatePlayer() {
        return PlayerUtilities.generatePlayer(this.getPlayerService());
    }

    protected Leaderboard generateLeaderboard() {
        return LeaderboardUtilities.generateLeaderboard(
                this.getLeaderboardService(),
                this.gameService,
                this.statService,
                this.boardService
        );
    }

    protected Filter<P> generateFilter() {
        final P player = this.generatePlayer();
        final Leaderboard leaderboard = this.generateLeaderboard();

        return this.generateFilter(player, leaderboard, Reason.GIVEN);
    }

    protected Filter<P> generateFilter(final Reason reason) {
        final P player = this.generatePlayer();
        final Leaderboard leaderboard = this.generateLeaderboard();

        return this.generateFilter(player, leaderboard, reason);
    }

    protected Filter<P> generateFilter(final P player, final Leaderboard leaderboard, final Reason reason) {
        final LocalDateTime filterStart = LocalDateTime.now();
        final LocalDateTime filterEnd = LocalDateTime.now().plusMinutes(ThreadLocalRandom.current().nextInt(5_000));

        return this.filterService.addFilter(player, leaderboard, reason, filterStart, filterEnd);
    }

    @Test
    void getFilters_all() {
        final List<Filter<P>> requiredFilters = new ArrayList<>();
        for (int count = 0; 5 >= count; count++) {
            requiredFilters.add(this.generateFilter());
        }

        final List<Filter<P>> foundFilters = this.filterService.getFilters();
        assertThat(foundFilters).containsAll(requiredFilters);
    }

    @Test
    void getFilters_player() {
        final Filter<P> filter = this.generateFilter();

        final List<Filter<P>> foundFilters = this.filterService.getFilters(filter.getPlayer());
        assertThat(foundFilters).containsOnly(filter);
    }

    @Test
    void getFilters_leaderboard() {
        final Filter<P> filter = this.generateFilter();

        final List<Filter<P>> foundFilters = this.filterService.getFilters(filter.getLeaderboard());
        assertThat(foundFilters).containsOnly(filter);
    }

    @Test
    void getFilters_player_leaderboard() {
        final Filter<P> filter = this.generateFilter();

        final List<Filter<P>> foundFilters = this.filterService.getFilters(filter.getPlayer(), filter.getLeaderboard());
        assertThat(foundFilters).containsOnly(filter);
    }

    @Test
    void getFilters_player_leaderboard_time() {
        final Filter<P> filter = this.generateFilter();

        final List<Filter<P>> foundFilters = this.filterService.getFilters(
                filter.getPlayer(),
                filter.getLeaderboard(),
                filter.getStart()
        );
        assertThat(foundFilters).containsOnly(filter);
    }

    @Test
    void isFiltered_player_leaderboard_time_equals_filter_start() {
        final Filter<P> filter = this.generateFilter();

        final boolean found = this.filterService.isFiltered(
                filter.getPlayer(),
                filter.getLeaderboard(),
                filter.getStart()
        );
        assertThat(found).isTrue();
    }

    @Test
    void isFiltered_player_leaderboard_time_equals_filter_end() {
        final Filter<P> filter = this.generateFilter();

        final boolean found = this.filterService.isFiltered(
                filter.getPlayer(),
                filter.getLeaderboard(),
                filter.getEnd()
        );
        assertThat(found).isTrue();
    }

    @ParameterizedTest
    @EnumSource(Reason.class)
    void isFiltered_player_leaderboard_time_reason(final Reason reason) {
        final Filter<P> filter = this.generateFilter(reason);

        final boolean found = this.filterService.isFiltered(
                filter.getPlayer(),
                filter.getLeaderboard(),
                filter.getEnd(),
                Collections.singleton(reason)
        );
        assertThat(found).isTrue();
    }

    @ParameterizedTest
    @EnumSource(Reason.class)
    void isFiltered_player_leaderboard_time_reason_not_found(final Reason reason) {
        final Filter<P> filter = this.generateFilter(reason);

        final Set<Reason> reasons = EnumSet.allOf(Reason.class);
        reasons.remove(reason);
        final boolean notFound = this.filterService.isFiltered(
                filter.getPlayer(),
                filter.getLeaderboard(),
                filter.getEnd(),
                reasons
        );
        assertThat(notFound).isFalse();
    }

    @Test
    void isFiltered_player_leaderboard() {
        final Filter<P> filter = this.generateFilter();

        final boolean found = this.filterService.isFiltered(
                filter.getPlayer(),
                filter.getLeaderboard()
        );
        assertThat(found).isTrue();
    }

    @Test
    void isFiltered_player_leaderboard_not_found() {
        final Leaderboard leaderboard = this.generateLeaderboard();
        final P player = this.generatePlayer();

        final boolean notFound = this.filterService.isFiltered(
                player,
                leaderboard
        );
        assertThat(notFound).isFalse();
    }

    @Test
    void addFilter() {
        final P player = this.generatePlayer();
        final Leaderboard leaderboard = this.generateLeaderboard();
        final Reason reason = Reason.SUSPECTED_HACKED;
        final LocalDateTime filterStart = LocalDateTime.now();
        final LocalDateTime filterEnd = LocalDateTime.now().plusMinutes(ThreadLocalRandom.current().nextInt(5_000));

        // Check that filter does not exist
        final boolean filterNotFound = this.filterService.isFiltered(player, leaderboard, filterStart);
        assertThat(filterNotFound).isFalse();

        // Create filter
        final Filter<P> filter = this.filterService.addFilter(player, leaderboard, reason, filterStart, filterEnd);

        // Verify that the content is the same
        assertThat(filter.getPlayer()).isEqualTo(player);
        assertThat(filter.getLeaderboard()).isEqualTo(leaderboard);
        assertThat(filter.getReason()).isEqualTo(reason);
        assertThat(filter.getStart()).isEqualToIgnoringNanos(filterStart);
        assertThat(filter.getEnd()).isEqualToIgnoringNanos(filterEnd);
    }

    @Test
    void removeFilter() {
        final Filter<P> filter = this.generateFilter();

        // Remove filter
        this.filterService.removeFilter(filter);

        // All filter objects have uniq player and leaderboard instances
        final boolean notFound = this.filterService.isFiltered(filter.getPlayer(), filter.getLeaderboard());
        assertThat(notFound).isFalse();
    }

    @Test
    void newInstance() {
        final Filter<P> filter = this.generateFilter();

        final F newFilterService = this.filterServiceSupplier.get();

        // Check that filter is loaded correctly from the repository
        final boolean filterFound = newFilterService.isFiltered(filter.getPlayer(), filter.getLeaderboard(), filter.getStart());
        assertThat(filterFound).isTrue();
    }
}