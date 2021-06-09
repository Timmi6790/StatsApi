package de.timmi6790.mpstats.api.versions.v1.common.filter.models;

import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models.Filter;
import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models.FilterDuration;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@lombok.Data
public class FilterCache {
    private final Map<Integer, List<FilterData>> filters = new ConcurrentHashMap<>();

    public void addFilter(final Filter<?> filter) {
        this.filters.computeIfAbsent(filter.getLeaderboard().getRepositoryId(), k -> Collections.synchronizedList(new ArrayList<>()))
                .add(
                        new FilterData(
                                filter.getRepositoryId(),
                                filter.getReason(),
                                filter.getFilterDuration()
                        )
                );
    }

    private List<FilterData> getFilterDataList(final Leaderboard leaderboard) {
        return this.filters.get(leaderboard.getRepositoryId());
    }

    private Optional<FilterData> getFilterData(final Leaderboard leaderboard, final ZonedDateTime timestamp) {
        final List<FilterData> filterDurations = this.getFilterDataList(leaderboard);
        if (filterDurations == null || filterDurations.isEmpty()) {
            return Optional.empty();
        }

        for (final FilterData filterDuration : filterDurations) {
            if (filterDuration.betweenDate(timestamp)) {
                return Optional.of(filterDuration);
            }
        }

        return Optional.empty();
    }

    public void removeFilter(final Filter<?> filter) {
        final int leaderBoardId = filter.getLeaderboard().getRepositoryId();
        final List<FilterData> foundFilters = this.filters.get(leaderBoardId);
        if (foundFilters != null) {
            foundFilters.removeIf(f -> f.getFilterId() == filter.getRepositoryId());
            if (foundFilters.isEmpty()) {
                this.filters.remove(leaderBoardId);
            }
        }
    }

    public boolean isFiltered(final Leaderboard leaderboard, final ZonedDateTime timestamp) {
        return this.getFilterData(leaderboard, timestamp).isPresent();
    }

    public boolean isFiltered(final Leaderboard leaderboard,
                              final ZonedDateTime timestamp,
                              final Set<Reason> allowedReasons) {
        return this.getFilterData(leaderboard, timestamp)
                .map(filterData -> allowedReasons.contains(filterData.getReason()))
                .orElse(Boolean.FALSE);
    }

    public boolean isFiltered(final Leaderboard leaderboard) {
        return this.filters.containsKey(leaderboard.getRepositoryId());
    }

    public int size() {
        int size = 0;
        for (final List<FilterData> values : this.filters.values()) {
            size += values.size();
        }
        return size;
    }

    @Data
    private static class FilterData {
        private final int filterId;
        private final Reason reason;
        @Nullable
        private final FilterDuration filterDuration;

        public boolean betweenDate(final ZonedDateTime timestamp) {
            // No filter duration indicates a permanent filter
            if (this.filterDuration == null) {
                return true;
            }

            return this.filterDuration.getStart().compareTo(timestamp) <= 0 && this.filterDuration.getEnd().compareTo(timestamp) >= 0;
        }
    }
}
