package de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models;

import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.NoArgsConstructor;

@NoArgsConstructor(force = true)
public class BedrockPlayer extends Player {
    public BedrockPlayer(final int repositoryId, final String name) {
        super(repositoryId, name);
    }
}
