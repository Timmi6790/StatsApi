package de.timmi6790.mpstats.api.versions.v1.common.filter.models;

import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models.Filter;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class FilterCache {
    private final Map<Integer, List<FilterDuration>> filters = new ConcurrentHashMap<>();

    public void addFilter(final Filter<?> filter) {
        this.filters.computeIfAbsent(filter.leaderboard().repositoryId(), k -> Collections.synchronizedList(new ArrayList<>()))
                .add(
                        new FilterDuration(
                                filter.repositoryId(),
                                filter.filterStart(),
                                filter.filterEnd()
                        )
                );
    }

    public void removeFilter(final Filter<?> filter) {
        final int leaderBoardId = filter.leaderboard().repositoryId();
        final List<FilterDuration> foundFilters = this.filters.get(leaderBoardId);
        if (foundFilters != null) {
            foundFilters.removeIf(f -> f.filterId() == filter.repositoryId());
            if (foundFilters.isEmpty()) {
                this.filters.remove(leaderBoardId);
            }
        }
    }

    public int size() {
        int size = 0;
        for (final List<FilterDuration> values : this.filters.values()) {
            size += values.size();
        }
        return size;
    }

    public boolean isFiltered(final Leaderboard leaderboard, final LocalDateTime timestamp) {
        final List<FilterDuration> filterDurations = this.filters.get(leaderboard.repositoryId());
        if (filterDurations.isEmpty()) {
            return false;
        }

        for (final FilterDuration filterDuration : filterDurations) {
            if (filterDuration.betweenDate(timestamp)) {
                return true;
            }
        }

        return false;
    }

    public boolean isFiltered(final Leaderboard leaderboard) {
        return this.filters.containsKey(leaderboard.repositoryId());
    }

    public record FilterDuration(int filterId, LocalDateTime filterStart, LocalDateTime filterEnd) {
        public boolean betweenDate(final LocalDateTime timestamp) {
            return this.filterStart.compareTo(timestamp) <= 0 && this.filterEnd.compareTo(timestamp) >= 0;
        }
    }
}
