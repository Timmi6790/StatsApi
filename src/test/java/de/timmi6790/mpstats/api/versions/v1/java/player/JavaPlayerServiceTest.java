package de.timmi6790.mpstats.api.versions.v1.java.player;

import de.timmi6790.mpstats.api.utilities.java.JavaServiceGenerator;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.JavaPlayerRepository;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaRepositoryPlayer;
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

        final Optional<JavaRepositoryPlayer> playerNotFound = javaPlayerService.getPlayer(playerName, playerUUID);
        assertThat(playerNotFound).isNotPresent();

        // Insert player
        final JavaRepositoryPlayer repositoryPlayer = javaPlayerService.getPlayerOrCreate(playerName, playerUUID);

        final Optional<JavaRepositoryPlayer> playerFound = javaPlayerService.getPlayer(playerName, playerUUID);
        assertThat(playerFound)
                .isPresent()
                .contains(repositoryPlayer);
    }

    @Test
    void getPlayer_by_id() {
        final String playerName = generatePlayerName();
        final UUID playerUUID = generatePlayerUUID();

        // Insert player
        final JavaRepositoryPlayer repositoryPlayer = javaPlayerService.getPlayerOrCreate(playerName, playerUUID);

        final Optional<JavaRepositoryPlayer> playerFound = javaPlayerService.getPlayer(repositoryPlayer.getRepositoryId());
        assertThat(playerFound)
                .isPresent()
                .contains(repositoryPlayer);
    }

    @Test
    void getPlayer_with_uuid_name_change() {
        final String playerName = generatePlayerName();
        final String newPlayerName = generatePlayerName();
        final UUID playerUUID = generatePlayerUUID();

        // Insert player
        javaPlayerService.getPlayerOrCreate(playerName, playerUUID);

        // Get with current name
        final Optional<JavaRepositoryPlayer> playerOldName = javaPlayerService.getPlayer(playerName, playerUUID);
        assertThat(playerOldName).isPresent();

        // Get with new name
        final Optional<JavaRepositoryPlayer> playerNewName = javaPlayerService.getPlayer(newPlayerName, playerUUID);
        assertThat(playerNewName).isPresent();

        assertThat(playerNewName.get().getPlayerName()).isEqualTo(newPlayerName);
    }

    @Test
    void getPlayerOrCreate() {
        final String playerName = generatePlayerName();
        final UUID playerUUID = generatePlayerUUID();

        final Optional<JavaRepositoryPlayer> playerNotFound = javaPlayerService.getPlayer(playerName, playerUUID);
        assertThat(playerNotFound).isNotPresent();

        final JavaRepositoryPlayer player = javaPlayerService.getPlayerOrCreate(playerName, playerUUID);
        assertThat(player.getPlayerUUID()).isEqualTo(playerUUID);
        assertThat(player.getPlayerName()).isEqualTo(playerName);

        // Cache check
        final JavaRepositoryPlayer playerCache = javaPlayerService.getPlayerOrCreate(playerName, playerUUID);
        assertThat(player).isEqualTo(playerCache);

        // No cache check
        final Optional<JavaRepositoryPlayer> playerNoCache = javaPlayerRepository.getPlayer(playerName, playerUUID);
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
        final JavaRepositoryPlayer playerChangedName = javaPlayerService.getPlayerOrCreate(newPlayerName, playerUUID);

        assertThat(playerChangedName.getPlayerName()).isEqualTo(newPlayerName);
    }

    @Test
    void getPlayer() {
        final String playerName = generatePlayerName();
        final UUID playerUUID = generatePlayerUUID();

        final Optional<JavaRepositoryPlayer> playerNotFound = javaPlayerService.getPlayer(playerUUID);
        assertThat(playerNotFound).isNotPresent();

        // Insert player
        javaPlayerService.getPlayerOrCreate(playerName, playerUUID);

        final Optional<JavaRepositoryPlayer> playerFound = javaPlayerService.getPlayer(playerUUID);
        assertThat(playerFound).isPresent();
    }
}