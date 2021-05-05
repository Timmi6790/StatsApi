package de.timmi6790.mpstats.api.versions.v1.java.player;

import de.timmi6790.mpstats.api.utilities.java.JavaServiceGenerator;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.JavaPlayerRepository;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static de.timmi6790.mpstats.api.utilities.PlayerUtilities.generatePlayerName;
import static de.timmi6790.mpstats.api.utilities.PlayerUtilities.generatePlayerUUID;
import static org.assertj.core.api.Assertions.assertThat;

class JavaPlayerServiceTest {
    private static JavaPlayerRepository javaPlayerRepository;
    private static JavaPlayerService javaPlayerService;

    @BeforeAll
    static void setUp() {
        javaPlayerService = JavaServiceGenerator.generatePlayerService();
        javaPlayerRepository = javaPlayerService.getPlayerRepository();
    }

    @Test
    void hasPlayer_with_uuid() {
        final String playerName = generatePlayerName();
        final UUID playerUUID = generatePlayerUUID();

        final boolean playerNotFound = javaPlayerService.hasPlayer(playerName, playerUUID);
        assertThat(playerNotFound).isFalse();

        // Insert player
        javaPlayerService.getPlayerOrCreate(playerName, playerUUID);

        final boolean playerFound = javaPlayerService.hasPlayer(playerName, playerUUID);
        assertThat(playerFound).isTrue();
    }

    @Test
    void getPlayer_with_uuid() {
        final String playerName = generatePlayerName();
        final UUID playerUUID = generatePlayerUUID();

        final Optional<JavaPlayer> playerNotFound = javaPlayerService.getPlayer(playerName, playerUUID);
        assertThat(playerNotFound).isNotPresent();

        // Insert player
        final JavaPlayer player = javaPlayerService.getPlayerOrCreate(playerName, playerUUID);

        final Optional<JavaPlayer> playerFound = javaPlayerService.getPlayer(playerName, playerUUID);
        assertThat(playerFound)
                .isPresent()
                .contains(player);
    }

    @Test
    void getPlayer_by_id() {
        final String playerName = generatePlayerName();
        final UUID playerUUID = generatePlayerUUID();

        // Insert player
        final JavaPlayer player = javaPlayerService.getPlayerOrCreate(playerName, playerUUID);

        final Optional<JavaPlayer> playerFound = javaPlayerService.getPlayer(player.getRepositoryId());
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
        javaPlayerService.getPlayerOrCreate(playerName, playerUUID);

        // Get with current name
        final Optional<JavaPlayer> playerOldName = javaPlayerService.getPlayer(playerName, playerUUID);
        assertThat(playerOldName).isPresent();

        // Get with new name
        final Optional<JavaPlayer> playerNewName = javaPlayerService.getPlayer(newPlayerName, playerUUID);
        assertThat(playerNewName).isPresent();

        assertThat(playerNewName.get().getName()).isEqualTo(newPlayerName);
    }

    @Test
    void getPlayerOrCreate() {
        final String playerName = generatePlayerName();
        final UUID playerUUID = generatePlayerUUID();

        final Optional<JavaPlayer> playerNotFound = javaPlayerService.getPlayer(playerName, playerUUID);
        assertThat(playerNotFound).isNotPresent();

        final JavaPlayer player = javaPlayerService.getPlayerOrCreate(playerName, playerUUID);
        assertThat(player.getRepositoryId()).isNotZero();
        assertThat(player.getUuid()).isEqualTo(playerUUID);
        assertThat(player.getName()).isEqualTo(playerName);

        // Cache check
        final JavaPlayer playerCache = javaPlayerService.getPlayerOrCreate(playerName, playerUUID);
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
        javaPlayerService.getPlayerOrCreate(playerName, playerUUID);
        // Get with new name
        final JavaPlayer playerChangedName = javaPlayerService.getPlayerOrCreate(newPlayerName, playerUUID);

        assertThat(playerChangedName.getName()).isEqualTo(newPlayerName);
    }

    @Test
    void getPlayer() {
        final String playerName = generatePlayerName();
        final UUID playerUUID = generatePlayerUUID();

        final Optional<JavaPlayer> playerNotFound = javaPlayerService.getPlayer(playerUUID);
        assertThat(playerNotFound).isNotPresent();

        // Insert player
        javaPlayerService.getPlayerOrCreate(playerName, playerUUID);

        final Optional<JavaPlayer> playerFound = javaPlayerService.getPlayer(playerUUID);
        assertThat(playerFound).isPresent();
    }
}