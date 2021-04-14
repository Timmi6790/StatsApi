package de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.RepositoryPlayer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BedrockRepositoryPlayer extends Player implements RepositoryPlayer {
    @JsonIgnore
    private final int repositoryId;

    public BedrockRepositoryPlayer(final String playerName, final int repositoryId) {
        super(playerName);

        this.repositoryId = repositoryId;
    }
}
