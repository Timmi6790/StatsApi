package de.timmi6790.mpstats.api.versions.v1.bedrock.player;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.util.concurrent.Striped;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.BedrockPlayerRepository;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockRepositoryPlayer;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Service
public class BedrockPlayerService implements PlayerService<BedrockRepositoryPlayer> {
    private final BedrockPlayerRepository bedrockPlayerRepository;

    private final Striped<Lock> playerLock = Striped.lock(128);
    private final Cache<String, BedrockRepositoryPlayer> playerCache = Caffeine.newBuilder()
            .expireAfterAccess(7, TimeUnit.MINUTES)
            .build();

    @Autowired
    public BedrockPlayerService(final BedrockPlayerRepository bedrockPlayerRepository) {
        this.bedrockPlayerRepository = bedrockPlayerRepository;
    }

    private Lock getPlayerLock(final String playerName) {
        return this.playerLock.get(playerName.toLowerCase());
    }

    private void addPlayerToCache(final BedrockRepositoryPlayer player) {
        this.playerCache.put(player.getPlayerName().toLowerCase(), player);
    }

    private Optional<BedrockRepositoryPlayer> getPlayerFromCache(final String playerName) {
        return Optional.ofNullable(this.playerCache.getIfPresent(playerName.toLowerCase()));
    }

    @Override
    public boolean hasPlayer(final String playerName) {
        return this.getPlayer(playerName).isPresent();
    }

    @Override
    public Optional<BedrockRepositoryPlayer> getPlayer(final String playerName) {
        // Cache check
        final Optional<BedrockRepositoryPlayer> playerCached = this.getPlayerFromCache(playerName);
        if (playerCached.isPresent()) {
            return playerCached;
        }

        final Optional<BedrockRepositoryPlayer> playerOpt = this.bedrockPlayerRepository.getPlayer(playerName);
        playerOpt.ifPresent(this::addPlayerToCache);
        return playerOpt;
    }

    public BedrockRepositoryPlayer getPlayerOrCreate(final String playerName) {
        final Lock lock = this.getPlayerLock(playerName);
        lock.lock();
        try {
            final Optional<BedrockRepositoryPlayer> playerOpt = this.getPlayer(playerName);
            if (playerOpt.isPresent()) {
                return playerOpt.get();
            }

            final BedrockRepositoryPlayer player = this.bedrockPlayerRepository.insertPlayer(playerName);
            this.addPlayerToCache(player);
            return player;
        } finally {
            lock.unlock();
        }
    }
}
