package de.timmi6790.mpstats.api.versions.v1.java.player;

import de.timmi6790.mpstats.api.utilities.java.JavaServiceGenerator;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.JavaPlayerRepository;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static de.timmi6790.mpstats.api.utilities.PlayerUtilities.*;
import static org.assertj.core.api.Assertions.assertThat;

class JavaPlayerServiceTest {
    private static JavaPlayerRepository javaPlayerRepository;
    private static JavaPlayerService playerService;

    @BeforeAll
    static void setUp() {
        playerService = JavaServiceGenerator.generatePlayerService();
        javaPlayerRepository = playerService.getPlayerRepository();
    }

    @Test
    void hasPlayer_with_uuid() {
        final String playerName = generatePlayerName();
        final UUID playerUUID = generatePlayerUUID();

        final boolean playerNotFound = playerService.hasPlayer(playerName, playerUUID);
        assertThat(playerNotFound).isFalse();

        // Insert player
        playerService.getPlayerOrCreate(playerName, playerUUID);

        final boolean playerFound = playerService.hasPlayer(playerName, playerUUID);
        assertThat(playerFound).isTrue();
    }

    @Test
    void getPlayer_with_uuid() {
        final String playerName = generatePlayerName();
        final UUID playerUUID = generatePlayerUUID();

        final Optional<JavaPlayer> playerNotFound = playerService.getPlayer(playerName, playerUUID);
        assertThat(playerNotFound).isNotPresent();

        // Insert player
        final JavaPlayer player = playerService.getPlayerOrCreate(playerName, playerUUID);

        final Optional<JavaPlayer> playerFound = playerService.getPlayer(playerName, playerUUID);
        assertThat(playerFound)
                .isPresent()
                .contains(player);
    }

    @Test
    void getPlayer_by_id() {
        final String playerName = generatePlayerName();
        final UUID playerUUID = generatePlayerUUID();

        // Insert player
        final JavaPlayer player = playerService.getPlayerOrCreate(playerName, playerUUID);

        final Optional<JavaPlayer> playerFound = playerService.getPlayer(player.getRepositoryId());
        assertThat(playerFound)
                .isPresent()
                .contains(player);
    }

    @Test
    void getPlayer_with_uuid_name_change() {
        final String playerName = generatePlayerName();
        final String newPlayerName = generatePlayerName();
        final UUID playerUUID = generatePlayerUUID();

        // Insert player
        playerService.getPlayerOrCreate(playerName, playerUUID);

        // Get with current name
        final Optional<JavaPlayer> playerOldName = playerService.getPlayer(playerName, playerUUID);
        assertThat(playerOldName).isPresent();

        // Get with new name
        final Optional<JavaPlayer> playerNewName = playerService.getPlayer(newPlayerName, playerUUID);
        assertThat(playerNewName).isPresent();

        assertThat(playerNewName.get().getName()).isEqualTo(newPlayerName);
    }

    @Test
    void getPlayerOrCreate() {
        final String playerName = generatePlayerName();
        final UUID playerUUID = generatePlayerUUID();

        final Optional<JavaPlayer> playerNotFound = playerService.getPlayer(playerName, playerUUID);
        assertThat(playerNotFound).isNotPresent();

        final JavaPlayer player = playerService.getPlayerOrCreate(playerName, playerUUID);
        assertThat(player.getRepositoryId()).isNotZero();
        assertThat(player.getUuid()).isEqualTo(playerUUID);
        assertThat(player.getName()).isEqualTo(playerName);

        // Cache check
        final JavaPlayer playerCache = playerService.getPlayerOrCreate(playerName, playerUUID);
        assertThat(player).isEqualTo(playerCache);

        // No cache check
        final Optional<JavaPlayer> playerNoCache = javaPlayerRepository.getPlayer(playerName, playerUUID);
        assertThat(playerNoCache)
                .isPresent()
                .contains(player);
    }

    @Test
    void getPlayerOrCreate_name_change() {
        final String playerName = generatePlayerName();
        final String newPlayerName = generatePlayerName();
        final UUID playerUUID = generatePlayerUUID();

        // Insert with old name
        playerService.getPlayerOrCreate(playerName, playerUUID);
        // Get with new name
        final JavaPlayer playerChangedName = playerService.getPlayerOrCreate(newPlayerName, playerUUID);

        assertThat(playerChangedName.getName()).isEqualTo(newPlayerName);
    }

    @Test
    void getPlayer() {
        final String playerName = generatePlayerName();
        final UUID playerUUID = generatePlayerUUID();

        final Optional<JavaPlayer> playerNotFound = playerService.getPlayer(playerUUID);
        assertThat(playerNotFound).isNotPresent();

        // Insert player
        playerService.getPlayerOrCreate(playerName, playerUUID);

        final Optional<JavaPlayer> playerFound = playerService.getPlayer(playerUUID);
        assertThat(playerFound).isPresent();
    }

    @Test
    void getPlayersOrCreate() {
        final Map<UUID, String> players = new HashMap<>();
        for (int count = 0; 20 > count; count++) {
            players.put(generatePlayerUUID(), generatePlayerName());
        }

        final Map<UUID, JavaPlayer> foundsPlayers = playerService.getPlayersOrCreate(players);
        assertThat(foundsPlayers).containsOnlyKeys(players.keySet());
        for (final Map.Entry<UUID, JavaPlayer> entry : foundsPlayers.entrySet()) {
            final UUID playerUUID = entry.getKey();
            final String expectedPlayerName = players.get(playerUUID);

            assertThat(playerUUID).isEqualTo(entry.getValue().getUuid());
            assertThat(expectedPlayerName).isEqualTo(entry.getValue().getName());
        }
    }

    @Test
    void getPlayersOrCreate_pre_created_players() {
        final Map<UUID, String> players = new HashMap<>();
        for (int count = 0; 20 > count; count++) {
            final String playerName = generatePlayerName();
            final UUID playerUUID = generatePlayerUUID();
            players.put(playerUUID, playerName);
            // Initialize a few players
            if (count % 2 == 0) {
                playerService.getPlayerOrCreate(playerName, playerUUID);
            }
        }

        final Map<UUID, JavaPlayer> foundsPlayers = playerService.getPlayersOrCreate(players);
        assertThat(foundsPlayers).containsOnlyKeys(players.keySet());
        for (final Map.Entry<UUID, JavaPlayer> entry : foundsPlayers.entrySet()) {
            final UUID playerUUID = entry.getKey();
            final String expectedPlayerName = players.get(playerUUID);

            assertThat(playerUUID).isEqualTo(entry.getValue().getUuid());
            assertThat(expectedPlayerName).isEqualTo(entry.getValue().getName());
        }
    }

    @Test
    void getPlayersOrCreate_pre_created_players_changed_names() {
        final Map<UUID, String> players = new HashMap<>();
        for (int count = 0; 20 > count; count++) {
            final String playerName = generatePlayerName();
            final UUID playerUUID = generatePlayerUUID();
            players.put(playerUUID, playerName);
            // Initialize a few players, with random names
            if (count % 2 == 0) {
                playerService.getPlayerOrCreate(generatePlayerName(), playerUUID);
            }
        }

        final Map<UUID, JavaPlayer> foundsPlayers = playerService.getPlayersOrCreate(players);
        assertThat(foundsPlayers).containsOnlyKeys(players.keySet());
        for (final Map.Entry<UUID, JavaPlayer> entry : foundsPlayers.entrySet()) {
            final UUID playerUUID = entry.getKey();
            final String expectedPlayerName = players.get(playerUUID);

            assertThat(playerUUID).isEqualTo(entry.getValue().getUuid());
            assertThat(expectedPlayerName).isEqualTo(entry.getValue().getName());
        }
    }

    @Test
    void getPlayersOrCreate_empty() {
        final Map<UUID, JavaPlayer> empty = playerService.getPlayersOrCreate(new HashMap<>());
        assertThat(empty).isEmpty();
    }

    @Test
    void getPlayers_by_id() {
        final Map<Integer, Player> players = new HashMap<>();
        for (int count = 0; 10 > count; count++) {
            final JavaPlayer player = generatePlayer(playerService);
            players.put(player.getRepositoryId(), player);
        }

        final Map<Integer, JavaPlayer> foundPlayers = playerService.getPlayers(players.keySet());
        assertThat(players).isEqualTo(foundPlayers);
    }
}