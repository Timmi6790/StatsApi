package de.timmi6790.mineplex_stats_api.versions.v1.bedrock.leaderboard.models;

import lombok.Data;

import java.util.List;

@Data
public class BedrockLeaderboardModel {
    private final Info info;
    private final List<BedrockLeaderboardEntry> entries;

    @Data
    public static class Info {
        private final String game;
        private final long copyUnixTime;
        private final boolean filter;
    }
}
