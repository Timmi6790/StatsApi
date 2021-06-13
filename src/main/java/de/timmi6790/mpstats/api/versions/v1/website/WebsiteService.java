package de.timmi6790.mpstats.api.versions.v1.website;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.website.models.GameStat;
import de.timmi6790.mpstats.api.versions.v1.website.models.WebsitePlayer;
import de.timmi6790.mpstats.api.versions.v1.website.parser.WebsiteParser;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class WebsiteService {
    private final AsyncLoadingCache<String, Optional<WebsitePlayer>> playerStatsCache;

    @Autowired
    public WebsiteService(final WebsiteParser websiteParser, final MeterRegistry registry) {
        this.playerStatsCache = Caffeine
                .newBuilder()
                .maximumSize(100)
                .recordStats()
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .buildAsync(websiteParser::retrievePlayerStats);

        CaffeineCacheMetrics.monitor(registry, this.playerStatsCache, "cache_website_player");
    }

    public CompletableFuture<Optional<WebsitePlayer>> retrievePlayer(final String playerName) {
        return this.playerStatsCache.get(playerName);
    }

    @SneakyThrows
    public Optional<WebsitePlayer> retrievePlayerSync(final String player) {
        try {
            return this.retrievePlayer(player).get(30, TimeUnit.SECONDS);
        } catch (final ExecutionException | TimeoutException e) {
            return Optional.empty();
        }
    }

    public CompletableFuture<Optional<GameStat>> retrievePlayerGameStats(final String playerName,
                                                                         final Game game) {
        return this.retrievePlayer(playerName).thenApply(playerDataOpt -> {
            if (playerDataOpt.isPresent()) {
                final WebsitePlayer playerData = playerDataOpt.get();
                return Optional.ofNullable(playerData.getGameStats().get(game));
            }

            return Optional.empty();
        });
    }

    @SneakyThrows
    public Optional<GameStat> retrievePlayerGameStatsSync(final String playerName, final Game game) {
        try {
            return this.retrievePlayerGameStats(playerName, game).get(30, TimeUnit.SECONDS);
        } catch (final ExecutionException | TimeoutException e) {
            return Optional.empty();
        }
    }
}
