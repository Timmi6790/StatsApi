package de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository;

import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.Player;

import java.util.Optional;

public interface BedrockPlayerRepository {
    Optional<Player> getPlayer(String playerName);

    Player insertPlayer(String playerName);
}
