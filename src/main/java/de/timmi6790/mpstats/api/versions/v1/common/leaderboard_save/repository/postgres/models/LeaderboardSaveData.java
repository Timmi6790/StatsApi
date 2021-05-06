package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.repository.postgres.models;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LeaderboardSaveData {
    private final int saveId;
    private final LocalDateTime saveTime;
}
