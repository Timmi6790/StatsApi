package de.timmi6790.mpstats.api.versions.v1.common.filter;

import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.FilterRepository;
import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models.Filter;
import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.postgres.FilterPostgresRepository;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.RepositoryPlayer;
import lombok.AccessLevel;
import lombok.Getter;
import org.jdbi.v3.core.Jdbi;

import java.time.LocalDateTime;
import java.util.List;

// TODO: Add caching?
@Getter(value = AccessLevel.PROTECTED)
public class FilterService<P extends Player & RepositoryPlayer, S extends PlayerService<P>> {
    private final S playerService;
    private final LeaderboardService leaderboardService;

    private final FilterRepository<P> filterRepository;

    public FilterService(final S playerService,
                         final LeaderboardService leaderboardService,
                         final Jdbi jdbi,
                         final String schema) {
        this.playerService = playerService;
        this.leaderboardService = leaderboardService;

        this.filterRepository = new FilterPostgresRepository<>(playerService, leaderboardService, jdbi, schema);
    }

    public List<Filter<P>> getFilters() {
        return this.filterRepository.getFilters();
    }

    public List<Filter<P>> getFilters(final P player) {
        return this.filterRepository.getFilters(player);
    }

    public List<Filter<P>> getFilters(final Leaderboard leaderboard) {
        return this.filterRepository.getFilters(leaderboard);
    }

    public List<Filter<P>> getFilters(final P player, final Leaderboard leaderboard) {
        return this.filterRepository.getFilters(player, leaderboard);
    }

    public List<Filter<P>> getFilters(final P player, final Leaderboard leaderboard, final LocalDateTime timestamp) {
        return this.filterRepository.getFilters(player, leaderboard, timestamp);
    }

    public boolean isFiltered(final P player, final Leaderboard leaderboard, final LocalDateTime timestamp) {
        return !this.getFilters(player, leaderboard, timestamp).isEmpty();
    }

    public boolean isFiltered(final P player, final Leaderboard leaderboard) {
        return !this.getFilters(player, leaderboard).isEmpty();
    }

    public Filter<P> addFilter(final P player,
                               final Leaderboard leaderboard,
                               final String reason,
                               final LocalDateTime filterStart,
                               final LocalDateTime filterEnd) {
        return this.filterRepository.addFilter(player, leaderboard, reason, filterStart, filterEnd);
    }

    public void removeFilter(final Filter<P> filter) {
        this.filterRepository.removeFilter(filter);
    }
}
