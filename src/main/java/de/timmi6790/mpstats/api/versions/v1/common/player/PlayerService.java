package de.timmi6790.mpstats.api.versions.v1.common.player;

import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface PlayerService<P extends Player> {
    boolean isValidPlayerName(final String playerName);

    boolean hasPlayer(final String playerName);

    Optional<P> getPlayer(final int repositoryId);

    Optional<P> getPlayer(final String playerName);

    Map<Integer, P> getPlayers(Collection<Integer> repositoryIds);
}
