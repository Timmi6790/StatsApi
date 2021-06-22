package de.timmi6790.mpstats.api.versions.v1.common.models;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class LeaderboardPlayerPositionSave<P extends Player> {
    private final Leaderboard leaderboard;
    private final ZonedDateTime saveTime;
    private final LeaderboardPositionEntry<P> entry;
}
