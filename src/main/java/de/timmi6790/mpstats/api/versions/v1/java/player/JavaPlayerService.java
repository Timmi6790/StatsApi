package de.timmi6790.mpstats.api.versions.v1.java.player;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.JavaPlayerRepository;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class JavaPlayerService {
    private final JavaPlayerRepository javaPlayerRepository;

    private final Cache<String, Player> playerCache = Caffeine.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build();

    @Autowired
    public JavaPlayerService(final JavaPlayerRepository javaPlayerRepository) {
        this.javaPlayerRepository = javaPlayerRepository;
    }

    public boolean hasPlayer(final String playerName) {
        return this.getPlayer(playerName).isPresent();
    }

    public Optional<Player> getPlayer(final String playerName) {
        return Optional.empty();
    }

    public Player getPlayerOrCreate(final String playerName) {
        return null;
    }

    public Player createPlayer(final String playerName) {
        return null;
    }

    public void removePlayer(final String playerName) {

    }
}
