package de.timmi6790.mpstats.api.versions.v1.java.player.repository;

import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.Player;

import java.util.Optional;
import java.util.UUID;

public interface JavaPlayerRepository {
    Optional<Player> getPlayer(UUID playerUUID);

    Optional<Player> getPlayer(String playerName, UUID playerUUID);

    Player insertPlayer(String playerName, UUID playerUUID);

    void changePlayerName(int playerId, String newName);
}
