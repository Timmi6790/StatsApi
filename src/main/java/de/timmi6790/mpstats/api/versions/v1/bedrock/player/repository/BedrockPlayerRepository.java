package de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository;


import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockPlayer;

import java.util.Optional;

public interface BedrockPlayerRepository {
    Optional<BedrockPlayer> getPlayer(int repositoryId);

    Optional<BedrockPlayer> getPlayer(String playerName);

    BedrockPlayer insertPlayer(String playerName);
}
