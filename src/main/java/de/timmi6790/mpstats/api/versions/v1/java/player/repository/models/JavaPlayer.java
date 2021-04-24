package de.timmi6790.mpstats.api.versions.v1.java.player.repository.models;

import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor(force = true)
public class JavaPlayer extends Player {
    private final UUID playerUUID;

    public JavaPlayer(final String playerName, final UUID playerUUID) {
        super(playerName);

        this.playerUUID = playerUUID;
    }
}
