package de.timmi6790.mpstats.api.versions.v1.common.group.repository.models;

import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.models.PlayerStats;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GroupPlayerStats<P extends Player> extends PlayerStats<P> {
    private final Group group;

    public GroupPlayerStats(final PlayerStats<P> playerStats,
                            final Group group) {
        super(playerStats.getPlayer(), playerStats.getGeneratedStats(), playerStats.getStats());

        this.group = group;
    }
}
