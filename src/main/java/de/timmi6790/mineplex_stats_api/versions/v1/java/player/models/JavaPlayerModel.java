package de.timmi6790.mineplex_stats_api.versions.v1.java.player.models;

import lombok.Data;

import java.util.UUID;

@Data
public class JavaPlayerModel {
    private final String playerName;
    private final UUID playerUUID;
}
