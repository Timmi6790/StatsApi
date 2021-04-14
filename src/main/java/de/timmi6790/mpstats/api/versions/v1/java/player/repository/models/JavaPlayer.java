package de.timmi6790.mpstats.api.versions.v1.java.player.repository.models;

import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JavaPlayer extends Player {
    private final UUID playerUUID;

    public JavaPlayer(final String playerName, final UUID playerUUID) {
        super(playerName);

        this.playerUUID = playerUUID;
    }
}
