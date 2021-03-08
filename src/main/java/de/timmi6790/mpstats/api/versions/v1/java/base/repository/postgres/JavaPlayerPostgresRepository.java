package de.timmi6790.mpstats.api.versions.v1.java.base.repository.postgres;

import de.timmi6790.mpstats.api.versions.v1.java.base.repository.JavaPlayerRepository;
import de.timmi6790.mpstats.api.versions.v1.java.base.repository.models.Player;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class JavaPlayerPostgresRepository implements JavaPlayerRepository {
    @Override
    public Optional<Player> getPlayer(final String playerName, final UUID playerUUID) {
        return Optional.empty();
    }

    @Override
    public Player insertPlayer(final String playerName, final UUID playerUUID) {
        return null;
    }

    @Override
    public void removePlayer(final int playerId) {

    }

    @Override
    public void changePlayerName(final int playerId, final String newName) {

    }
}
