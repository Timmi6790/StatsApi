package de.timmi6790.mineplex_stats_api.versions.v1.bedrock.model;

import lombok.Data;

@Data
public class BedrockLeaderboardEntry {
    private final String playerName;
    private final int position;
    private final long score;
}
