package de.timmi6790.mpstats.api.versions.v1.java.player.repository;

import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface JavaPlayerRepository {
    Optional<JavaPlayer> getPlayer(int repositoryId);

    Optional<JavaPlayer> getPlayer(UUID playerUUID);

    Optional<JavaPlayer> getPlayer(String playerName, UUID playerUUID);

    JavaPlayer insertPlayer(String playerName, UUID playerUUID);

    void changePlayerName(int playerId, String newName);

    Map<UUID, JavaPlayer> getPlayersOrCreate(final Map<UUID, String> players);

    Map<Integer, JavaPlayer> getPlayers(Collection<Integer> repositoryIds);
}
