package de.timmi6790.mineplex_stats_api.versions.v1.website.models;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class WebsitePlayerModel {
    private final String playerName;
    private final UUID playerUUID;
    private final String primaryRank;
    private final Map<String, Map<String, Long>> stats;
}
