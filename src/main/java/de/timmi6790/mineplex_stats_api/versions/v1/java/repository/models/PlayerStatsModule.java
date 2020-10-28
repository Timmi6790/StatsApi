package de.timmi6790.mineplex_stats_api.versions.v1.java.repository.models;

import de.timmi6790.commons.utilities.UUIDUtilities;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class PlayerStatsModule {
    private final UUID playerUUID;
    private final String playerName;
    private final String game;
    private final String board;
    private final List<StatEntry> entries = new ArrayList<>();

    @ConstructorProperties({"playerUUID", "playerName", "game", "board"})
    public PlayerStatsModule(final byte[] playerUUID, final String playerName, final String game, final String board) {
        this.playerUUID = UUIDUtilities.getUUIDFromBytes(playerUUID);
        this.playerName = playerName;
        this.game = game;
        this.board = board;
    }

    @Data
    @AllArgsConstructor
    public static class StatEntry {
        private final String stat;
        private final int position;
        private final long score;
        private final long unixTime;
    }
}
