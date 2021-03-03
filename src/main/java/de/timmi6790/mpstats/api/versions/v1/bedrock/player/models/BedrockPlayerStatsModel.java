package de.timmi6790.mpstats.api.versions.v1.bedrock.player.models;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BedrockPlayerStatsModel {
    private final Info info;
    private final List<GeneratedStat> generatedStats;
    private final List<DatabaseStats> leaderboardStats;

    @Data
    public static class Info {
        private final String playerName;
        private final LocalDateTime requestedDateTime;
        private final boolean filter;
    }

    @Data
    public static class GeneratedStat {
        private final String stat;
        private final double value;
    }

    @Data
    public static class DatabaseStats {
        private final String game;
        private final long score;
        private final int unixTimestamp;
    }
}
