package de.timmi6790.mpstats.api.versions.v1.java.player;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.util.concurrent.Striped;
import de.timmi6790.api.mojang.MojangApiClient;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.JavaPlayerRepository;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
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
public class JavaPlayerService implements PlayerService<JavaPlayer> {
    private static final Pattern NAME_PATTERN = Pattern.compile("^\\w{1,16}$");

    @Getter(AccessLevel.PROTECTED)
    private final JavaPlayerRepository playerRepository;

    private final Striped<Lock> playerLock = Striped.lock(1_024);
    private final Cache<UUID, JavaPlayer> playerCache = Caffeine.newBuilder()
            .expireAfterAccess(7, TimeUnit.MINUTES)
            .recordStats()
            .build();

    @Autowired
    public JavaPlayerService(final JavaPlayerRepository playerRepository, final MeterRegistry registry) {
        this.playerRepository = playerRepository;

        CaffeineCacheMetrics.monitor(registry, this.playerCache, "cache_java_player");
    }

    private Lock getPlayerLock(final UUID playerUUID) {
        return this.playerLock.get(playerUUID);
    }

    public boolean hasPlayer(final String playerName, final UUID playerUUID) {
        return this.getPlayer(playerName, playerUUID).isPresent();
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
    public Optional<JavaPlayer> getPlayer(final int repositoryId) {
        return this.playerRepository.getPlayer(repositoryId);
    }

    public Optional<JavaPlayer> getPlayer(final UUID playerUUID) {
        // Cache check
        final JavaPlayer playerCached = this.playerCache.getIfPresent(playerUUID);
        if (playerCached != null) {
            return Optional.of(playerCached);
        }

        final Optional<JavaPlayer> playerOpt = this.playerRepository.getPlayer(playerUUID);
        playerOpt.ifPresent(player -> this.playerCache.put(playerUUID, player));
        return playerOpt;
    }

    public Optional<JavaPlayer> getPlayer(final String playerName, final UUID playerUUID) {
        // Cache check
        final JavaPlayer playerCached = this.playerCache.getIfPresent(playerUUID);
        if (playerCached != null) {
            // Check if the player name changed while we had the cache
            if (!playerCached.getName().equals(playerName)) {
                playerCached.setName(playerName);
                this.playerRepository.changePlayerName(playerCached.getRepositoryId(), playerName);
            }

            return Optional.of(playerCached);
        }

        final Optional<JavaPlayer> playerOpt = this.playerRepository.getPlayer(playerName, playerUUID);
        playerOpt.ifPresent(player -> this.playerCache.put(playerUUID, player));
        return playerOpt;
    }

    @Override
    public Optional<JavaPlayer> getPlayer(final String playerName) {
        return MojangApiClient.getInstance().getPlayerInfo(playerName)
                .flatMap(playerInfo -> this.getPlayer(playerInfo.getName(), playerInfo.getUuid()));
    }

    @Override
    public Map<Integer, JavaPlayer> getPlayers(final Collection<Integer> repositoryIds) {
        return this.playerRepository.getPlayers(repositoryIds);
    }

    public JavaPlayer getPlayerOrCreate(final String playerName, final UUID playerUUID) {
        final Lock lock = this.getPlayerLock(playerUUID);
        lock.lock();
        try {
            final Optional<JavaPlayer> playerOpt = this.getPlayer(playerName, playerUUID);
            if (playerOpt.isPresent()) {
                return playerOpt.get();
            }

            final JavaPlayer player = this.playerRepository.insertPlayer(playerName, playerUUID);
            this.playerCache.put(player.getUuid(), player);
            return player;
        } finally {
            lock.unlock();
        }
    }

    public Map<UUID, JavaPlayer> getPlayersOrCreate(final Map<UUID, String> players) {
        if (players.isEmpty()) {
            return new HashMap<>();
        }

        return this.playerRepository.getPlayersOrCreate(players);
    }
}
