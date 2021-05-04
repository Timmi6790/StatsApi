package de.timmi6790.mpstats.api.versions.v1.common.filter.models;

import de.timmi6790.mpstats.api.versions.v1.common.filter.Reason;
import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models.Filter;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@lombok.Data
public class FilterCache {
    private final Map<Integer, List<Data>> filters = new ConcurrentHashMap<>();

    public void addFilter(final Filter<?> filter) {
        this.filters.computeIfAbsent(filter.leaderboard().getRepositoryId(), k -> Collections.synchronizedList(new ArrayList<>()))
                .add(
                        new Data(
                                filter.repositoryId(),
                                filter.reason(),
                                filter.start(),
                                filter.end()
                        )
                );
    }

    private List<Data> getFilterDataList(final Leaderboard leaderboard) {
        return this.filters.get(leaderboard.getRepositoryId());
    }

    private Optional<Data> getFilterData(final Leaderboard leaderboard, final LocalDateTime timestamp) {
        final List<Data> filterDurations = this.getFilterDataList(leaderboard);
        if (filterDurations.isEmpty()) {
            return Optional.empty();
        }

        for (final Data filterDuration : filterDurations) {
            if (filterDuration.betweenDate(timestamp)) {
                return Optional.of(filterDuration);
            }
        }

        return Optional.empty();
    }

    public void removeFilter(final Filter<?> filter) {
        final int leaderBoardId = filter.leaderboard().getRepositoryId();
        final List<Data> foundFilters = this.filters.get(leaderBoardId);
        if (foundFilters != null) {
            foundFilters.removeIf(f -> f.filterId() == filter.repositoryId());
            if (foundFilters.isEmpty()) {
                this.filters.remove(leaderBoardId);
            }
        }
    }

    public boolean isFiltered(final Leaderboard leaderboard, final LocalDateTime timestamp) {
        return this.getFilterData(leaderboard, timestamp).isPresent();
    }

    public boolean isFiltered(final Leaderboard leaderboard,
                              final LocalDateTime timestamp,
                              final Collection<Reason> allowedReasons) {
        return this.getFilterData(leaderboard, timestamp)
                .map(data -> allowedReasons.contains(data.reason()))
                .orElse(Boolean.FALSE);
    }

    public boolean isFiltered(final Leaderboard leaderboard) {
        return this.filters.containsKey(leaderboard.getRepositoryId());
    }

    public int size() {
        int size = 0;
        for (final List<Data> values : this.filters.values()) {
            size += values.size();
        }
        return size;
    }

    private record Data(int filterId, Reason reason, LocalDateTime filterStart, LocalDateTime filterEnd) {
        public boolean betweenDate(final LocalDateTime timestamp) {
            return this.filterStart.compareTo(timestamp) <= 0 && this.filterEnd.compareTo(timestamp) >= 0;
        }
    }
}
