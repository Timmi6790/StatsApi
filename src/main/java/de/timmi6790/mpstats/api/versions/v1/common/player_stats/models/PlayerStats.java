package de.timmi6790.mpstats.api.versions.v1.common.player_stats.models;

import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.Data;

import java.util.Set;

@Data
public class PlayerStats<P extends Player> {
    private final P player;
    private final Set<GeneratedPlayerEntry> generatedStats;
    private final Set<PlayerEntry> stats;
}
