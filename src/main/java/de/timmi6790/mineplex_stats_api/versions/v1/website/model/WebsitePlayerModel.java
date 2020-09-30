package de.timmi6790.mineplex_stats_api.versions.v1.website.model;

import de.timmi6790.mineplex_stats_api.versions.v1.models.StatModel;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class WebsitePlayerModel {
    private final String playerName;
    private final UUID playerUUID;
    private final String rank;
    private final Map<String, List<StatModel>> stats;
}
