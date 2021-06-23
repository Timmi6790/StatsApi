package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.repository.postgres.models;


import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class LeaderboardSaveData {
    private final int leaderboardId;
    private final int saveId;
    private final ZonedDateTime saveTime;
}
