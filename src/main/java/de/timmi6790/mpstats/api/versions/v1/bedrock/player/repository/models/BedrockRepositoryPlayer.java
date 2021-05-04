package de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.RepositoryPlayer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(force = true)
public class BedrockRepositoryPlayer extends BedrockPlayer implements RepositoryPlayer {
    @JsonIgnore
    private final int repositoryId;

    public BedrockRepositoryPlayer(final int repositoryId, final String playerName) {
        super(playerName);

        this.repositoryId = repositoryId;
    }
}
