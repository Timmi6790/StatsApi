package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.models;

import lombok.Data;

@Data
public class PlayerData {
    private final int playerRepositoryId;
    private final long score;
}
