package de.timmi6790.mpstats.api.versions.v1.java.filter;

import de.timmi6790.mpstats.api.versions.v1.common.filter.FilterService;
import de.timmi6790.mpstats.api.versions.v1.common.filter.models.FilterCache;
import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models.Filter;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.JavaLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.java.player.JavaPlayerService;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaRepositoryPlayer;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class JavaFilterService extends FilterService<JavaRepositoryPlayer, JavaPlayerService> {
    private final Map<UUID, Integer> uuidToIdPlayerMap = new HashMap<>();

    @Autowired
    public JavaFilterService(final Jdbi jdbi,
                             final JavaPlayerService playerService,
                             final JavaLeaderboardService leaderboardService) {
        super(playerService, leaderboardService, jdbi, "java");
        this.loadRepositoryEntriesIntoCache();
    }

    protected Optional<FilterCache> getFilterCache(final UUID playerUUID) {
        final Integer playerId = this.uuidToIdPlayerMap.get(playerUUID);
        if (playerId == null) {
            return Optional.empty();
        }

        return this.getFilterCache(playerId);
    }

    @Override
    protected void addFilterToCache(final Filter<JavaRepositoryPlayer> filter) {
        this.uuidToIdPlayerMap.put(filter.player().getPlayerUUID(), filter.player().getRepositoryId());
        super.addFilterToCache(filter);
    }

    @Override
    protected void removeFilterFromCache(final Filter<JavaRepositoryPlayer> filter) {
        this.uuidToIdPlayerMap.remove(filter.player().getPlayerUUID());
        super.removeFilterFromCache(filter);
    }

    public boolean isFiltered(final UUID playerUUID, final Leaderboard leaderboard, final LocalDateTime timestamp) {
        return this.getFilterCache(playerUUID)
                .map(cache -> cache.isFiltered(leaderboard, timestamp))
                .orElse(Boolean.FALSE);
    }

    public boolean isFiltered(final UUID playerUUID, final Leaderboard leaderboard) {
        return this.getFilterCache(playerUUID)
                .map(cache -> cache.isFiltered(leaderboard))
                .orElse(Boolean.FALSE);
    }
}
