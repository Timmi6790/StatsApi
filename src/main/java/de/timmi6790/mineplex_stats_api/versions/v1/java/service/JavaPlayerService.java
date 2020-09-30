package de.timmi6790.mineplex_stats_api.versions.v1.java.service;

import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Service;

@Service
public class JavaPlayerService {
    private static final String SET_PLAYER_ID_NAME = "SET @playerId = (SELECT id FROM java_player WHERE player_name = :playerName LIMIT 1);";
    private static final String SET_PLAYER_ID_UUID = "SET @playerId = (SELECT id FROM java_player WHERE uuid = :uuid LIMIT 1);";

    private final Jdbi database;

    public JavaPlayerService(final Jdbi jdbi) {
        this.database = jdbi;
    }
}
