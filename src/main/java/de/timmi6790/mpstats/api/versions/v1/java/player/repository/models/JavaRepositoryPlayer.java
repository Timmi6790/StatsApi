package de.timmi6790.mpstats.api.versions.v1.java.player.repository.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.RepositoryPlayer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class JavaRepositoryPlayer extends JavaPlayer implements RepositoryPlayer {
    @JsonIgnore
    private final int repositoryId;

    public JavaRepositoryPlayer(final int repositoryId, final String playerName, final UUID playerUUID) {
        super(playerName, playerUUID);

        this.repositoryId = repositoryId;
    }
}
