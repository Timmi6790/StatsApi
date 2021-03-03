package de.timmi6790.mineplex_stats_api.versions.v1.java.player.models;

import de.timmi6790.mineplex_stats_api.versions.v1.models.StatModel;
import de.timmi6790.mineplex_stats_api.versions.v1.models.StatPositionModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class PlayerStatsModel {
    private final Info info;
    private final Map<String, GeneratedStat> generatedStats;
    private final Map<String, PlayerLeaderboardStat> leaderboardStats;
    private final Map<String, StatModel> websiteStats;

    @Data
    public static class Info {
        private final UUID playerUUID;
        private final String playerName;
        private final String game;
        private final String board;
        private final LocalDateTime requestedDateTime;
        private final boolean filter;
    }

    @Data
    public static class GeneratedStat {
        private final String stat;
        private final double value;
    }

    @EqualsAndHashCode(callSuper = true)
    @ToString
    @Getter
    public static class PlayerLeaderboardStat extends StatPositionModel {
        private final long unixTime;

        public PlayerLeaderboardStat(final String stat, final long score, final int position, final long unixTime) {
            super(stat, score, position);

            this.unixTime = unixTime;
        }
    }
}
