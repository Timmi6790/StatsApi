package de.timmi6790.mpstats.api.versions.v1.common.player;

import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.RepositoryPlayer;

import java.util.Optional;

public interface PlayerService<PLAYER extends Player & RepositoryPlayer> {
    boolean hasPlayer(final String playerName);

    Optional<PLAYER> getPlayer(final String playerName);
}
