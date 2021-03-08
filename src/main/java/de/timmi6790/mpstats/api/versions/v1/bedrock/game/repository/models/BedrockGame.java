package de.timmi6790.mpstats.api.versions.v1.bedrock.game.repository.models;

import lombok.Data;

@Data
public class BedrockGame {
    private final String name;
    private final boolean removedGame;
}
