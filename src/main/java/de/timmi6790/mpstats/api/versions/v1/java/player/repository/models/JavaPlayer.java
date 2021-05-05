package de.timmi6790.mpstats.api.versions.v1.java.player.repository.models;

import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;


@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor(force = true)
public class JavaPlayer extends Player {
    private final UUID uuid;

    public JavaPlayer(final int repositoryId, final String name, final UUID uuid) {
        super(repositoryId, name);
        this.uuid = uuid;
    }
}
