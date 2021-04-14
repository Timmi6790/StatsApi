package de.timmi6790.mpstats.api.versions.v1.website;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.timmi6790.mpstats.api.versions.v1.website.models.WebsitePlayerModel;
import de.timmi6790.mpstats.api.versions.v1.website.parser.WebsiteParser;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class WebsiteService {
    private final AsyncLoadingCache<String, Optional<WebsitePlayerModel>> playerStatsCache;

    @Autowired
    public WebsiteService(final WebsiteParser websiteParser) {
        this.playerStatsCache = Caffeine
                .newBuilder()
                .buildAsync(websiteParser::retrievePlayerStats);
    }

    public CompletableFuture<Optional<WebsitePlayerModel>> retrievePlayer(final String player) {
        return this.playerStatsCache.get(player);
    }

    @SneakyThrows
    public Optional<WebsitePlayerModel> retrievePlayerSync(final String player) {
        try {
            return this.retrievePlayer(player).get(30, TimeUnit.SECONDS);
        } catch (final ExecutionException | TimeoutException e) {
            return Optional.empty();
        }
    }

    public CompletableFuture<Optional<Map<String, Long>>> retrievePlayerGameStats(final String player,
                                                                                  final String gameName) {
        return CompletableFuture.supplyAsync(() -> {
            final Optional<WebsitePlayerModel> playerDataOpt = this.retrievePlayerSync(player);
            if (playerDataOpt.isPresent()) {
                final WebsitePlayerModel playerData = playerDataOpt.get();
                return Optional.ofNullable(playerData.getStats().get(gameName));
            }

            return Optional.empty();
        });
    }

    @SneakyThrows
    public Optional<Map<String, Long>> retrievePlayerGameStatsSync(final String player, final String gameName) {
        try {
            return this.retrievePlayerGameStats(player, gameName).get(30, TimeUnit.SECONDS);
        } catch (final ExecutionException | TimeoutException e) {
            return Optional.empty();
        }
    }
}
