package de.timmi6790.mpstats.api.versions.v1.java.base.repository;

import de.timmi6790.mpstats.api.versions.v1.java.base.repository.models.Player;

import java.util.Optional;
import java.util.UUID;

public interface JavaPlayerRepository {
    Optional<Player> getPlayer(String playerName, UUID playerUUID);

    Player insertPlayer(String playerName, UUID playerUUID);

    void removePlayer(int playerId);

    void changePlayerName(int playerId, String newName);
}
