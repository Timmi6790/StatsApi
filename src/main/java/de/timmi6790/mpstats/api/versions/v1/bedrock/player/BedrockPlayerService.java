package de.timmi6790.mpstats.api.versions.v1.bedrock.player;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.util.concurrent.Striped;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.BedrockPlayerRepository;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Service
public class BedrockPlayerService {
    private final BedrockPlayerRepository bedrockPlayerRepository;

    private final Striped<Lock> playerLock = Striped.lock(128);
    private final Cache<String, Player> playerCache = Caffeine.newBuilder()
            .expireAfterAccess(7, TimeUnit.MINUTES)
            .build();

    @Autowired
    public BedrockPlayerService(final BedrockPlayerRepository bedrockPlayerRepository) {
        this.bedrockPlayerRepository = bedrockPlayerRepository;
    }

    private Lock getPlayerLock(final String playerName) {
        return this.playerLock.get(playerName.toLowerCase());
    }

    private void addPlayerToCache(final Player player) {
        this.playerCache.put(player.getPlayerName().toLowerCase(), player);
    }

    private Optional<Player> getPlayerFromCache(final String playerName) {
        return Optional.ofNullable(this.playerCache.getIfPresent(playerName.toLowerCase()));
    }

    public boolean hasPlayer(final String playerName) {
        return this.getPlayer(playerName).isPresent();
    }

    public Optional<Player> getPlayer(final String playerName) {
        // Cache check
        final Optional<Player> playerCached = this.getPlayerFromCache(playerName);
        if (playerCached.isPresent()) {
            return playerCached;
        }

        final Optional<Player> playerOpt = this.bedrockPlayerRepository.getPlayer(playerName);
        playerOpt.ifPresent(this::addPlayerToCache);
        return playerOpt;
    }

    public Player getPlayerOrCreate(final String playerName) {
        final Lock lock = this.getPlayerLock(playerName);
        lock.lock();
        try {
            final Optional<Player> playerOpt = this.getPlayer(playerName);
            if (playerOpt.isPresent()) {
                return playerOpt.get();
            }

            final Player player = this.bedrockPlayerRepository.insertPlayer(playerName);
            this.addPlayerToCache(player);
            return player;
        } finally {
            lock.unlock();
        }
    }
}
