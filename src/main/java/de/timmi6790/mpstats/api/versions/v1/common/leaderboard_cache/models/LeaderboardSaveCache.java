package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_cache.models;

import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class LeaderboardSaveCache<P extends Player> {
    private final LocalDateTime saveTime;
    private final List<LeaderboardEntry<P>> entries;
}
