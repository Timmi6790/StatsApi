package de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.postgres;

import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.BedrockPlayerRepository;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockPlayer;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.postgres.mappers.PlayerMapper;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BedrockPlayerPostgresRepository implements BedrockPlayerRepository {
    private static final String SELECT_PLAYER = "SELECT id player_id, player_name player_name FROM bedrock.players WHERE player_name = :playerName LIMIT 1;";
    private static final String SELECT_PLAYER_BY_ID = "SELECT id player_id, player_name player_name FROM bedrock.players WHERE id = :repositoryId LIMIT 1;";
    private static final String INSERT_PLAYER = "INSERT INTO bedrock.players(player_name) VALUES(:playerName) RETURNING id player_id, player_name player_name;";

    private final Jdbi database;

    @Autowired
    public BedrockPlayerPostgresRepository(final Jdbi database) {
        this.database = database;

        database.registerRowMapper(new PlayerMapper());
    }

    @Override
    public Optional<BedrockPlayer> getPlayer(final int repositoryId) {
        return this.database.withHandle(handle ->
                handle.createQuery(SELECT_PLAYER_BY_ID)
                        .bind("repositoryId", repositoryId)
                        .mapTo(BedrockPlayer.class)
                        .findFirst()
        );
    }

    @Override
    public Optional<BedrockPlayer> getPlayer(final String playerName) {
        return this.database.withHandle(handle ->
                handle.createQuery(SELECT_PLAYER)
                        .bind("playerName", playerName)
                        .mapTo(BedrockPlayer.class)
                        .findFirst()
        );
    }

    @Override
    public BedrockPlayer insertPlayer(final String playerName) {
        return this.database.withHandle(handle ->
                handle.createQuery(INSERT_PLAYER)
                        .bind("playerName", playerName)
                        .mapTo(BedrockPlayer.class)
                        .first()
        );
    }
}
