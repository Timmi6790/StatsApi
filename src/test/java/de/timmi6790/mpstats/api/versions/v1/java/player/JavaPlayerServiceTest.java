package de.timmi6790.mpstats.api.versions.v1.java.player;

import de.timmi6790.mpstats.api.AbstractIntegrationTest;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.JavaPlayerRepository;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.Player;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.postgres.JavaPlayerPostgresRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class JavaPlayerServiceTest {
    private static JavaPlayerRepository javaPlayerRepository;
    private static JavaPlayerService javaPlayerService;

    private static final AtomicInteger PLAYER_ID = new AtomicInteger(0);

    @BeforeAll
    static void setUp() {
        javaPlayerRepository = new JavaPlayerPostgresRepository(AbstractIntegrationTest.jdbi());
        javaPlayerService = new JavaPlayerService(javaPlayerRepository);
    }

    private String generatePlayerName() {
        return "PLAYER" + PLAYER_ID.incrementAndGet();
    }

    private UUID generatePlayerUUID() {
        return UUID.randomUUID();
    }

    @Test
    void hasPlayer_with_uuid() {
        final String playerName = this.generatePlayerName();
        final UUID playerUUID = this.generatePlayerUUID();

        final boolean playerNotFound = javaPlayerService.hasPlayer(playerName, playerUUID);
        assertThat(playerNotFound).isFalse();

        // Insert player
        javaPlayerService.getPlayerOrCreate(playerName, playerUUID);

        final boolean playerFound = javaPlayerService.hasPlayer(playerName, playerUUID);
        assertThat(playerFound).isTrue();
    }

    @Test
    void getPlayer_with_uuid() {
        final String playerName = this.generatePlayerName();
        final UUID playerUUID = this.generatePlayerUUID();

        final Optional<Player> playerNotFound = javaPlayerService.getPlayer(playerName, playerUUID);
        assertThat(playerNotFound).isNotPresent();

        // Insert player
        javaPlayerService.getPlayerOrCreate(playerName, playerUUID);

        final Optional<Player> playerFound = javaPlayerService.getPlayer(playerName, playerUUID);
        assertThat(playerFound).isPresent();
    }

    @Test
    void getPlayer_with_uuid_name_change() {
        final String playerName = this.generatePlayerName();
        final String newPlayerName = this.generatePlayerName();
        final UUID playerUUID = this.generatePlayerUUID();

        // Insert player
        javaPlayerService.getPlayerOrCreate(playerName, playerUUID);

        // Get with current name
        final Optional<Player> playerOldName = javaPlayerService.getPlayer(playerName, playerUUID);
        assertThat(playerOldName).isPresent();

        // Get with new name
        final Optional<Player> playerNewName = javaPlayerService.getPlayer(newPlayerName, playerUUID);
        assertThat(playerNewName).isPresent();

        assertThat(playerNewName.get().getPlayerName()).isEqualTo(newPlayerName);
    }

    @Test
    void getPlayerOrCreate() {
        final String playerName = this.generatePlayerName();
        final UUID playerUUID = this.generatePlayerUUID();

        final Optional<Player> playerNotFound = javaPlayerService.getPlayer(playerName, playerUUID);
        assertThat(playerNotFound).isNotPresent();

        final Player player = javaPlayerService.getPlayerOrCreate(playerName, playerUUID);
        assertThat(player.getPlayerUUID()).isEqualTo(playerUUID);
        assertThat(player.getPlayerName()).isEqualTo(playerName);

        // Cache check
        final Player playerCache = javaPlayerService.getPlayerOrCreate(playerName, playerUUID);
        assertThat(player).isEqualTo(playerCache);

        // No cache check
        final Optional<Player> playerNoCache = javaPlayerRepository.getPlayer(playerName, playerUUID);
        assertThat(playerNoCache)
                .isPresent()
                .contains(player);
    }

    @Test
    void getPlayerOrCreate_name_change() {
        final String playerName = this.generatePlayerName();
        final String newPlayerName = this.generatePlayerName();
        final UUID playerUUID = this.generatePlayerUUID();

        // Insert with old name
        javaPlayerService.getPlayerOrCreate(playerName, playerUUID);
        // Get with new name
        final Player playerChangedName = javaPlayerService.getPlayerOrCreate(newPlayerName, playerUUID);

        assertThat(playerChangedName.getPlayerName()).isEqualTo(newPlayerName);
    }
}