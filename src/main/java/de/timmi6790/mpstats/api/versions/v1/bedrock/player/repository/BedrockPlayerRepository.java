package de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository;


import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockPlayer;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface BedrockPlayerRepository {
    Optional<BedrockPlayer> getPlayer(int repositoryId);

    Optional<BedrockPlayer> getPlayer(String playerName);

    BedrockPlayer insertPlayer(String playerName);

    Map<String, BedrockPlayer> getPlayersOrCreate(Set<String> playerNames);
}
