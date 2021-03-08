package de.timmi6790.mpstats.api.versions.v1.java.player_stats.models;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class PlayerStatsRatioModel {
    private final Info info;
    private final List<RatioEntry> entries;

    @Data
    public static class Info {
        private final UUID playerUUID;
        private final String playerName;
        private final String stat;
        private final String board;
        private final long totalValue;
        private final LocalDateTime requestedDateTime;
        private final boolean filter;
    }

    @Data
    public static class RatioEntry {
        private final String game;
        private final long value;
    }
}
