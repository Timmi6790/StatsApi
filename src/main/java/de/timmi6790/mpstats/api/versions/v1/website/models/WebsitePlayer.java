package de.timmi6790.mpstats.api.versions.v1.website.models;

import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class WebsitePlayer {
    private final String playerName;
    private final UUID playerUUID;
    private final String primaryRank;
    private final Map<Game, GameStat> gameStats;
}
