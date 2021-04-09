package de.timmi6790.mpstats.api.versions.v1.java.player;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.util.concurrent.Striped;
import de.timmi6790.mpstats.api.mojang.MojangApi;
import de.timmi6790.mpstats.api.mojang.models.MojangPlayer;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.JavaPlayerRepository;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Service
public class JavaPlayerService {
    private final JavaPlayerRepository javaPlayerRepository;

    private final Striped<Lock> playerLock = Striped.lock(128);
    private final Cache<UUID, Player> playerCache = Caffeine.newBuilder()
            .expireAfterAccess(7, TimeUnit.MINUTES)
            .build();

    @Autowired
    public JavaPlayerService(final JavaPlayerRepository javaPlayerRepository) {
        this.javaPlayerRepository = javaPlayerRepository;
    }

    private Lock getPlayerLock(final UUID playerUUID) {
        return this.playerLock.get(playerUUID);
    }

    public boolean hasPlayer(final String playerName, final UUID playerUUID) {
        return this.getPlayer(playerName, playerUUID).isPresent();
    }

    public boolean hasPlayer(final String playerName) {
        return this.getPlayer(playerName).isPresent();
    }

    public Optional<Player> getPlayer(final String playerName, final UUID playerUUID) {
        // Cache check
        final Player playerCached = this.playerCache.getIfPresent(playerUUID);
        if (playerCached != null) {
            // Check if the player name changed while we had the cache
            if (!playerCached.getPlayerName().equals(playerName)) {
                playerCached.setPlayerName(playerName);
                this.javaPlayerRepository.changePlayerName(playerCached.getRepositoryId(), playerName);
            }

            return Optional.of(playerCached);
        }

        final Optional<Player> playerOpt = this.javaPlayerRepository.getPlayer(playerName, playerUUID);
        playerOpt.ifPresent(player -> this.playerCache.put(playerUUID, player));
        return playerOpt;
    }

    public Optional<Player> getPlayer(final String playerName) {
        final Optional<MojangPlayer> mojangPlayerOpt = MojangApi.getPlayer(playerName);
        if (mojangPlayerOpt.isPresent()) {
            final MojangPlayer mojangPlayer = mojangPlayerOpt.get();
            return this.getPlayer(mojangPlayer.getName(), mojangPlayer.getUuid());
        }

        return Optional.empty();
    }

    public Player getPlayerOrCreate(final String playerName, final UUID playerUUID) {
        final Lock lock = this.getPlayerLock(playerUUID);
        lock.lock();
        try {
            final Optional<Player> playerOpt = this.getPlayer(playerName, playerUUID);
            if (playerOpt.isPresent()) {
                return playerOpt.get();
            }

            final Player player = this.javaPlayerRepository.insertPlayer(playerName, playerUUID);
            this.playerCache.put(player.getPlayerUUID(), player);
            return player;
        } finally {
            lock.unlock();
        }
    }
}
