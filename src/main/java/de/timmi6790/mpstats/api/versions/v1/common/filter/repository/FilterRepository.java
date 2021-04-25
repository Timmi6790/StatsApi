package de.timmi6790.mpstats.api.versions.v1.common.filter.repository;

import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models.Filter;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.RepositoryPlayer;

import java.time.LocalDateTime;
import java.util.List;

public interface FilterRepository<P extends Player & RepositoryPlayer> {
    List<Filter<P>> getFilters();

    List<Filter<P>> getFilters(P player);

    List<Filter<P>> getFilters(Leaderboard leaderboard);

    List<Filter<P>> getFilters(P player, Leaderboard leaderboard);

    List<Filter<P>> getFilters(P player, Leaderboard leaderboard, LocalDateTime timestamp);

    Filter<P> addFilter(P player, Leaderboard leaderboard, String reason, LocalDateTime filterStart, LocalDateTime filterEnd);

    void removeFilter(Filter<P> filter);
}
