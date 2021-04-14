package de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository;


import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockRepositoryPlayer;

import java.util.Optional;

public interface BedrockPlayerRepository {
    Optional<BedrockRepositoryPlayer> getPlayer(String playerName);

    BedrockRepositoryPlayer insertPlayer(String playerName);
}
