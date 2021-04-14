package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_request.models;

import lombok.Data;

@Data
public class WebLeaderboard {
    private final String player;
    private final long score;
}
