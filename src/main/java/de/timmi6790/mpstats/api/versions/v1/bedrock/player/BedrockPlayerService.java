package de.timmi6790.mpstats.api.versions.v1.bedrock.player;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.util.concurrent.Striped;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.BedrockPlayerRepository;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockPlayer;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.regex.Pattern;

@Service
public class BedrockPlayerService implements PlayerService<BedrockPlayer> {
    private static final Pattern NAME_PATTERN = Pattern.compile("^.{3,32}$");

    @Getter(AccessLevel.PROTECTED)
    private final BedrockPlayerRepository playerRepository;

    private final Striped<Lock> playerLock = Striped.lock(512);
    private final Cache<String, BedrockPlayer> playerCache = Caffeine.newBuilder()
            .expireAfterAccess(7, TimeUnit.MINUTES)
            .recordStats()
            .build();

    @Autowired
    public BedrockPlayerService(final BedrockPlayerRepository playerRepository, final MeterRegistry registry) {
        this.playerRepository = playerRepository;

        CaffeineCacheMetrics.monitor(registry, this.playerCache, "cache_bedrock_player");
    }

    private Lock getPlayerLock(final String playerName) {
        return this.playerLock.get(playerName.toLowerCase());
    }

    private void addPlayerToCache(final BedrockPlayer player) {
        this.playerCache.put(player.getName().toLowerCase(), player);
    }

    private Optional<BedrockPlayer> getPlayerFromCache(final String playerName) {
        return Optional.ofNullable(this.playerCache.getIfPresent(playerName.toLowerCase()));
    }

    @Override
    public boolean isValidPlayerName(final String playerName) {
        return NAME_PATTERN.matcher(playerName).find();
    }

    @Override
    public boolean hasPlayer(final String playerName) {
        return this.getPlayer(playerName).isPresent();
    }

    @Override
    public Optional<BedrockPlayer> getPlayer(final int repositoryId) {
        return this.playerRepository.getPlayer(repositoryId);
    }

    @Override
    public Optional<BedrockPlayer> getPlayer(final String playerName) {
        // Cache check
        final Optional<BedrockPlayer> playerCached = this.getPlayerFromCache(playerName);
        if (playerCached.isPresent()) {
            return playerCached;
        }

        final Optional<BedrockPlayer> playerOpt = this.playerRepository.getPlayer(playerName);
        playerOpt.ifPresent(this::addPlayerToCache);
        return playerOpt;
    }

    @Override
    public Map<Integer, BedrockPlayer> getPlayers(final Collection<Integer> repositoryIds) {
        return this.playerRepository.getPlayers(repositoryIds);
    }

    public BedrockPlayer getPlayerOrCreate(final String playerName) {
        final Lock lock = this.getPlayerLock(playerName);
        lock.lock();
        try {
            final Optional<BedrockPlayer> playerOpt = this.getPlayer(playerName);
            if (playerOpt.isPresent()) {
                return playerOpt.get();
            }

            final BedrockPlayer player = this.playerRepository.insertPlayer(playerName);
            this.addPlayerToCache(player);
            return player;
        } finally {
            lock.unlock();
        }
    }

    public Map<String, BedrockPlayer> getPlayersOrCreate(final Set<String> playerNames) {
        if (playerNames.isEmpty()) {
            return new HashMap<>();
        }

        return this.playerRepository.getPlayersOrCreate(playerNames);
    }
}
