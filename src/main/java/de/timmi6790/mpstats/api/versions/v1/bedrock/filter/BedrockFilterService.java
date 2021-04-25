package de.timmi6790.mpstats.api.versions.v1.bedrock.filter;

import de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard.BedrockLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.BedrockPlayerService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockRepositoryPlayer;
import de.timmi6790.mpstats.api.versions.v1.common.filter.FilterService;
import de.timmi6790.mpstats.api.versions.v1.common.filter.models.FilterCache;
import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models.Filter;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class BedrockFilterService extends FilterService<BedrockRepositoryPlayer, BedrockPlayerService> {
    private final Map<String, Integer> nameToIdPlayerMap = new HashMap<>();

    @Autowired
    public BedrockFilterService(final Jdbi jdbi,
                                final BedrockPlayerService playerService,
                                final BedrockLeaderboardService leaderboardService) {
        super(playerService, leaderboardService, jdbi, "bedrock");
        this.loadRepositoryEntriesIntoCache();
    }

    protected Optional<FilterCache> getFilterCache(final String playerName) {
        final Integer playerId = this.nameToIdPlayerMap.get(playerName);
        if (playerId == null) {
            return Optional.empty();
        }

        return this.getFilterCache(playerId);
    }

    @Override
    protected void addFilterToCache(final Filter<BedrockRepositoryPlayer> filter) {
        this.nameToIdPlayerMap.put(filter.player().getPlayerName(), filter.player().getRepositoryId());
        super.addFilterToCache(filter);
    }

    @Override
    protected void removeFilterFromCache(final Filter<BedrockRepositoryPlayer> filter) {
        this.nameToIdPlayerMap.remove(filter.player().getPlayerName());
        super.removeFilterFromCache(filter);
    }

    public boolean isFiltered(final String playerName, final Leaderboard leaderboard, final LocalDateTime timestamp) {
        return this.getFilterCache(playerName)
                .map(cache -> cache.isFiltered(leaderboard, timestamp))
                .orElse(false);
    }

    public boolean isFiltered(final String playerName, final Leaderboard leaderboard) {
        return this.getFilterCache(playerName)
                .map(cache -> cache.isFiltered(leaderboard))
                .orElse(false);
    }
}
