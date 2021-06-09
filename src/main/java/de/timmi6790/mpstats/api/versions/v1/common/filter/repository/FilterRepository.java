package de.timmi6790.mpstats.api.versions.v1.common.filter.repository;

import de.timmi6790.mpstats.api.versions.v1.common.filter.models.Reason;
import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models.Filter;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

public interface FilterRepository<P extends Player> {
    void addFilterReasons(Collection<String> filterReasons);

    List<Filter<P>> getFilters();

    List<Filter<P>> getFilters(P player);

    List<Filter<P>> getFilters(Leaderboard leaderboard);

    List<Filter<P>> getFilters(P player, Leaderboard leaderboard);

    List<Filter<P>> getFilters(P player, Leaderboard leaderboard, ZonedDateTime timestamp);

    Filter<P> addFilter(P player, Leaderboard leaderboard, Reason reason, @Nullable ZonedDateTime filterStart, @Nullable ZonedDateTime filterEnd);

    void removeFilter(Filter<P> filter);
}
