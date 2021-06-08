package de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.postgres;

import com.google.common.collect.Sets;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.BedrockPlayerRepository;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockPlayer;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.postgres.mappers.PlayerMapper;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BedrockPlayerPostgresRepository implements BedrockPlayerRepository {
    private static final String SELECT_PLAYER_BASE = "SELECT id player_id, player_name player_name FROM bedrock.players %s;";
    private static final String SELECT_PLAYER = String.format(SELECT_PLAYER_BASE, "WHERE LOWER(player_name) = LOWER(:playerName) LIMIT 1");
    private static final String SELECT_PLAYER_BY_ID = String.format(SELECT_PLAYER_BASE, "WHERE id = :repositoryId LIMIT 1");
    private static final String SELECT_PLAYER_BY_NAMES = String.format(SELECT_PLAYER_BASE, "WHERE LOWER(player_name) IN (<playerNames>);");
    private static final String SELECT_PLAYER_BY_IDS = String.format(SELECT_PLAYER_BASE, "WHERE id IN (<repositoryIds>);");
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

    @Override
    public synchronized Map<String, BedrockPlayer> getPlayersOrCreate(final Set<String> playerNames) {
        return this.database.withHandle(handle -> {
            // Convert all names to lower to avoid creating duplicate names
            final Set<String> lowerNames = Sets.newHashSetWithExpectedSize(playerNames.size());
            for (final String playerName : playerNames) {
                lowerNames.add(playerName.toLowerCase());
            }

            final Map<String, BedrockPlayer> foundPlayers = handle.createQuery(SELECT_PLAYER_BY_NAMES)
                    .bindList("playerNames", lowerNames)
                    .mapTo(BedrockPlayer.class)
                    .stream()
                    .collect(
                            Collectors.toMap(
                                    BedrockPlayer::getName,
                                    p -> p,
                                    (bedrockPlayer, bedrockPlayer2) -> bedrockPlayer,
                                    () -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER)
                            )
                    );

            // Insert new players
            final Set<String> newPlayerNames = new HashSet<>();
            final PreparedBatch newPlayersBatch = handle.prepareBatch(INSERT_PLAYER);
            for (final String playerName : playerNames) {
                if (!foundPlayers.containsKey(playerName)) {
                    newPlayerNames.add(playerName.toLowerCase());
                    newPlayersBatch.bind("playerName", playerName);
                    newPlayersBatch.add();
                }
            }
            if (newPlayersBatch.size() > 0) {
                newPlayersBatch.execute();
                final Map<String, BedrockPlayer> newPlayers = handle.createQuery(SELECT_PLAYER_BY_NAMES)
                        .bindList("playerNames", newPlayerNames)
                        .mapTo(BedrockPlayer.class)
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        BedrockPlayer::getName,
                                        p -> p,
                                        (bedrockPlayer, bedrockPlayer2) -> bedrockPlayer,
                                        () -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER)
                                )
                        );
                // Combine the two maps into one
                foundPlayers.putAll(newPlayers);
            }
            return foundPlayers;
        });
    }

    @Override
    public Map<Integer, BedrockPlayer> getPlayers(final Collection<Integer> repositoryIds) {
        if (repositoryIds.isEmpty()) {
            return new HashMap<>();
        }

        return this.database.withHandle(handle ->
                handle.createQuery(SELECT_PLAYER_BY_IDS)
                        .bindList("repositoryIds", repositoryIds)
                        .mapTo(BedrockPlayer.class)
                        .stream()
                        .collect(Collectors.toMap(BedrockPlayer::getRepositoryId, p -> p))
        );
    }
}
