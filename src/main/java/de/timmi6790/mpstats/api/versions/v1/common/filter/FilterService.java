package de.timmi6790.mpstats.api.versions.v1.common.filter;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Striped;
import de.timmi6790.mpstats.api.versions.v1.common.filter.models.FilterCache;
import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.FilterRepository;
import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models.Filter;
import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.postgres.FilterPostgresRepository;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jdbi.v3.core.Jdbi;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.Lock;

@Getter(AccessLevel.PROTECTED)
@Log4j2
public class FilterService<P extends Player, S extends PlayerService<P>> {
    private final S playerService;
    private final LeaderboardService leaderboardService;

    private final FilterRepository<P> filterRepository;

    private final Striped<Lock> filterCacheLock = Striped.lock(64);
    private final Map<Integer, FilterCache> filterCache = new HashMap<>();

    private final String schema;

    public FilterService(final S playerService,
                         final LeaderboardService leaderboardService,
                         final Jdbi jdbi,
                         final String schema) {
        this.schema = schema;
        this.playerService = playerService;
        this.leaderboardService = leaderboardService;

        this.filterRepository = new FilterPostgresRepository<>(playerService, leaderboardService, jdbi, schema);

        final List<String> reasons = Lists.newArrayListWithCapacity(Reason.values().length);
        for (final Reason reason : Reason.values()) {
            reasons.add(reason.name());
        }
        this.filterRepository.addFilterReasons(reasons);
    }

    protected void loadRepositoryEntriesIntoCache() {
        log.info("[{}] Load filters from repository", this.schema);
        final List<Filter<P>> filters = this.getFilters();
        for (final Filter<P> filter : filters) {
            this.addFilterToCache(filter);
        }
        log.info("[{}] Loaded {} filters from repository", this.schema, filters.size());
    }

    protected Lock getFilterCacheLock(final int playerId) {
        return this.filterCacheLock.get(playerId);
    }

    protected void addFilterToCache(final Filter<P> filter) {
        final Lock lock = this.getFilterCacheLock(filter.player().getRepositoryId());
        lock.lock();

        try {
            log.debug("[{}] Add {} to cache", this.schema, filter);
            this.filterCache.computeIfAbsent(filter.player().getRepositoryId(), k -> new FilterCache())
                    .addFilter(filter);
        } finally {
            lock.unlock();
        }
    }

    protected void removeFilterFromCache(final Filter<P> filter) {
        final FilterCache cacheEntry = this.filterCache.get(filter.player().getRepositoryId());
        if (cacheEntry == null) {
            return;
        }

        final Lock lock = this.getFilterCacheLock(filter.player().getRepositoryId());
        lock.lock();

        try {
            log.debug("[{}] Remove {} from cache", this.schema, filter);
            cacheEntry.removeFilter(filter);
            if (cacheEntry.size() <= 0) {
                this.filterCache.remove(filter.player().getRepositoryId());
            }
        } finally {
            lock.unlock();
        }
    }

    protected Optional<FilterCache> getFilterCache(final int playerId) {
        final Lock lock = this.getFilterCacheLock(playerId);
        lock.lock();

        try {
            return Optional.ofNullable(this.filterCache.get(playerId));
        } finally {
            lock.unlock();
        }
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
        return this.getFilterCache(player.getRepositoryId())
                .map(cache -> cache.isFiltered(leaderboard, timestamp))
                .orElse(Boolean.FALSE);
    }

    public boolean isFiltered(final P player,
                              final Leaderboard leaderboard,
                              final LocalDateTime timestamp,
                              final Collection<Reason> allowedReasons) {
        return this.getFilterCache(player.getRepositoryId())
                .map(cache -> cache.isFiltered(leaderboard, timestamp, allowedReasons))
                .orElse(Boolean.FALSE);
    }

    public boolean isFiltered(final P player, final Leaderboard leaderboard) {
        return this.getFilterCache(player.getRepositoryId())
                .map(cache -> cache.isFiltered(leaderboard))
                .orElse(Boolean.FALSE);
    }

    public Filter<P> addFilter(final P player,
                               final Leaderboard leaderboard,
                               final Reason reason,
                               final LocalDateTime filterStart,
                               final LocalDateTime filterEnd) {
        final Filter<P> filter = this.filterRepository.addFilter(player, leaderboard, reason, filterStart, filterEnd);
        this.addFilterToCache(filter);
        log.info("[{}] Created new filter {}", this.schema, filter);
        return filter;
    }

    public void removeFilter(final Filter<P> filter) {
        this.filterRepository.removeFilter(filter);
        this.removeFilterFromCache(filter);
        log.info("[{}] Removed filter {}", this.schema, filter);
    }
}
