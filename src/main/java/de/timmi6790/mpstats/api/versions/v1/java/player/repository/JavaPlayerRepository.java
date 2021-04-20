package de.timmi6790.mpstats.api.versions.v1.java.player.repository;

import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaRepositoryPlayer;

import java.util.Optional;
import java.util.UUID;

public interface JavaPlayerRepository {
    Optional<JavaRepositoryPlayer> getPlayer(int repositoryId);

    Optional<JavaRepositoryPlayer> getPlayer(UUID playerUUID);

    Optional<JavaRepositoryPlayer> getPlayer(String playerName, UUID playerUUID);

    JavaRepositoryPlayer insertPlayer(String playerName, UUID playerUUID);

    void changePlayerName(int playerId, String newName);
}
