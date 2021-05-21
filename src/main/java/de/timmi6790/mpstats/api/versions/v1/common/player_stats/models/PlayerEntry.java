package de.timmi6790.mpstats.api.versions.v1.common.player_stats.models;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class PlayerEntry {
    private final Leaderboard leaderboard;
    private final ZonedDateTime saveTime;
    private final long score;
    private final int position;

    public boolean isPresent() {
        return this.score != -1;
    }
}
