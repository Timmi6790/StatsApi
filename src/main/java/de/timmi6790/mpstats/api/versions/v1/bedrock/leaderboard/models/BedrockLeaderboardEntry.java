package de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard.models;

import lombok.Data;

@Data
public class BedrockLeaderboardEntry {
    private final String playerName;
    private final int position;
    private final long score;
}
