package de.timmi6790.mpstats.api.versions.v1.common.models;

import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.Data;

@Data
public class LeaderboardEntry<P extends Player> {
    private final P player;
    private final long score;
}
