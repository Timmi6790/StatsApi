package de.timmi6790.mpstats.api.versions.v1.website.models;

import java.util.Map;
import java.util.UUID;

public record WebsitePlayerModel(String playerName,
                                 UUID playerUUID,
                                 String primaryRank,
                                 Map<String, Map<String, Long>> stats) {
}
