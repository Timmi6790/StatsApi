package de.timmi6790.mpstats.api.versions.v1.common.models;

import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Getter
@ToString(callSuper = true)
public class LeaderboardPositionEntry<P extends Player> extends LeaderboardEntry<P> {
    private final int position;

    public LeaderboardPositionEntry(final LeaderboardEntry<P> entry, final int position) {
        this(entry.getPlayer(), entry.getScore(), position);
    }

    public LeaderboardPositionEntry(final P player, final long score, final int position) {
        super(player, score);

        this.position = position;
    }
}
