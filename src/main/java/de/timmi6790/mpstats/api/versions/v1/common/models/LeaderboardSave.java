package de.timmi6790.mpstats.api.versions.v1.common.models;

import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class LeaderboardSave<P extends Player> {
    private final LocalDateTime saveTime;
    private final List<LeaderboardEntry<P>> entries;
}
