package de.timmi6790.mpstats.api.versions.v1.java.player.repository.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.RepositoryPlayer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JavaRepositoryPlayer extends JavaPlayer implements RepositoryPlayer {
    @JsonIgnore
    private final int repositoryId;

    public JavaRepositoryPlayer(final int repositoryId, final String playerName, final UUID playerUUID) {
        super(playerName, playerUUID);

        this.repositoryId = repositoryId;
    }
}
