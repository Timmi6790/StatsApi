package de.timmi6790.mpstats.api.versions.v1.java.player.repository.postgres;

import de.timmi6790.mpstats.api.versions.v1.java.player.repository.JavaPlayerRepository;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.postgres.mappers.PlayerMapper;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class JavaPlayerPostgresRepository implements JavaPlayerRepository {
    private static final String SELECT_PLAYER = "SELECT id player_id, player_name player_name, player_uuid player_uuid FROM java.players WHERE player_uuid = :playerUUID LIMIT 1;";
    private static final String SELECT_PLAYER_BY_ID = "SELECT id player_id, player_name player_name, player_uuid player_uuid FROM java.players WHERE id = :repositoryId LIMIT 1;";
    private static final String UPDATE_PLAYER_NAME = "UPDATE java.players SET player_name = :playerName WHERE id = :playerId;";
    private static final String INSERT_PLAYER = "INSERT INTO java.players(player_name, player_uuid) VALUES(:playerName, :playerUUID) RETURNING id player_id, player_name player_name, player_uuid player_uuid;";

    private final Jdbi database;

    @Autowired
    public JavaPlayerPostgresRepository(final Jdbi database) {
        this.database = database;

        database.registerRowMapper(new PlayerMapper());
    }

    @Override
    public Optional<JavaPlayer> getPlayer(final int repositoryId) {
        return this.database.withHandle(handle ->
                handle.createQuery(SELECT_PLAYER_BY_ID)
                        .bind("repositoryId", repositoryId)
                        .mapTo(JavaPlayer.class)
                        .findFirst()
        );
    }

    @Override
    public Optional<JavaPlayer> getPlayer(final UUID playerUUID) {
        return this.database.withHandle(handle ->
                handle.createQuery(SELECT_PLAYER)
                        .bind("playerUUID", playerUUID)
                        .mapTo(JavaPlayer.class)
                        .findFirst()
        );
    }

    @Override
    public Optional<JavaPlayer> getPlayer(final String playerName, final UUID playerUUID) {
        final Optional<JavaPlayer> playerOpt = this.getPlayer(playerUUID);
        // Assure that the name was not changed
        if (playerOpt.isPresent()) {
            final JavaPlayer player = playerOpt.get();
            if (!player.getName().equals(playerName)) {
                this.changePlayerName(player.getRepositoryId(), playerName);
                player.setName(playerName);
            }
        }
        return playerOpt;
    }

    @Override
    public JavaPlayer insertPlayer(final String playerName, final UUID playerUUID) {
        return this.database.withHandle(handle ->
                handle.createQuery(INSERT_PLAYER)
                        .bind("playerName", playerName)
                        .bind("playerUUID", playerUUID)
                        .mapTo(JavaPlayer.class)
                        .first()
        );
    }

    @Override
    public void changePlayerName(final int playerId, final String newName) {
        this.database.useHandle(handle ->
                handle.createUpdate(UPDATE_PLAYER_NAME)
                        .bind("playerId", playerId)
                        .bind("playerName", newName)
                        .execute()
        );
    }
}
