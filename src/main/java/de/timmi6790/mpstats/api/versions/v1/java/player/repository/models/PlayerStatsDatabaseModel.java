package de.timmi6790.mpstats.api.versions.v1.java.player.repository.models;

import de.timmi6790.commons.utilities.UUIDUtilities;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.beans.ConstructorProperties;
import java.util.Map;
import java.util.UUID;

@Data
public class PlayerStatsDatabaseModel {
    private final UUID playerUUID;
    private final String playerName;
    private final String game;
    private final String board;
    private final Map<String, StatEntry> entries = new LinkedCaseInsensitiveMap<>();

    @ConstructorProperties({"playerUUID", "playerName", "game", "board"})
    public PlayerStatsDatabaseModel(final byte[] playerUUID, final String playerName, final String game, final String board) {
        this.playerUUID = UUIDUtilities.getUUIDFromBytes(playerUUID);
        this.playerName = playerName;
        this.game = game;
        this.board = board;
    }

    public void addEntry(final StatEntry entry) {
        this.entries.put(entry.getStat(), entry);
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
