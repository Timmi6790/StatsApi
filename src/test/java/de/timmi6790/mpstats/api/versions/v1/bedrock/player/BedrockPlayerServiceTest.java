package de.timmi6790.mpstats.api.versions.v1.bedrock.player;

import de.timmi6790.mpstats.api.utilities.bedrock.BedrockServiceGenerator;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.BedrockPlayerRepository;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockPlayer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static de.timmi6790.mpstats.api.utilities.PlayerUtilities.generatePlayerName;
import static org.assertj.core.api.Assertions.assertThat;

class BedrockPlayerServiceTest {
    private static BedrockPlayerRepository bedrockPlayerRepository;
    private static BedrockPlayerService playerService;

    @BeforeAll
    static void setUp() {
        playerService = BedrockServiceGenerator.generatePlayerService();
        bedrockPlayerRepository = playerService.getPlayerRepository();
    }

    @Test
    void hasPlayer() {
        final String playerName = generatePlayerName();

        final boolean playerNotFound = playerService.hasPlayer(playerName);
        assertThat(playerNotFound).isFalse();

        // Insert player
        playerService.getPlayerOrCreate(playerName);

        final boolean playerFound = playerService.hasPlayer(playerName);
        assertThat(playerFound).isTrue();
    }

    @Test
    void hasPlayer_ignore_case() {
        final String playerName = generatePlayerName();

        // Insert player
        playerService.getPlayerOrCreate(playerName);

        // Uppercase check
        final boolean playerUpper = playerService.hasPlayer(playerName.toUpperCase());
        assertThat(playerUpper).isTrue();

        // Lowercase check
        final boolean playerLower = playerService.hasPlayer(playerName.toLowerCase());
        assertThat(playerLower).isTrue();
    }

    @Test
    void getPlayer() {
        final String playerName = generatePlayerName();

        final Optional<BedrockPlayer> playerNotFound = playerService.getPlayer(playerName);
        assertThat(playerNotFound).isNotPresent();

        // Insert player
        playerService.getPlayerOrCreate(playerName);

        final Optional<BedrockPlayer> playerFound = playerService.getPlayer(playerName);
        assertThat(playerFound).isPresent();
    }

    @Test
    void getPlayer_by_id() {
        final String playerName = generatePlayerName();

        // Insert player
        final BedrockPlayer player = playerService.getPlayerOrCreate(playerName);

        final Optional<BedrockPlayer> playerFound = playerService.getPlayer(player.getRepositoryId());
        assertThat(playerFound)
                .isPresent()
                .contains(player);
    }

    @Test
    void getPlayer_ignore_case() {
        final String playerName = generatePlayerName();

        // Insert player
        playerService.getPlayerOrCreate(playerName);

        // Uppercase check
        final Optional<BedrockPlayer> playerUpper = playerService.getPlayer(playerName.toUpperCase());
        assertThat(playerUpper).isPresent();

        // Lowercase check
        final Optional<BedrockPlayer> playerLower = playerService.getPlayer(playerName.toLowerCase());
        assertThat(playerLower).isPresent();
    }

    @Test
    void getPlayerOrCreate() {
        final String playerName = generatePlayerName();

        final Optional<BedrockPlayer> playerNotFound = playerService.getPlayer(playerName);
        assertThat(playerNotFound).isNotPresent();

        final BedrockPlayer player = playerService.getPlayerOrCreate(playerName);
        assertThat(player.getName()).isEqualTo(playerName);

        // Cache check
        final BedrockPlayer playerCache = playerService.getPlayerOrCreate(playerName);
        assertThat(player).isEqualTo(playerCache);

        // No cache check
        final Optional<BedrockPlayer> playerNoCache = bedrockPlayerRepository.getPlayer(playerName);
        assertThat(playerNoCache)
                .isPresent()
                .contains(player);
    }

    @Test
    void getPlayersOrCreate() {
        final Set<String> playerNames = new HashSet<>();
        for (int count = 0; 20 > count; count++) {
            playerNames.add(generatePlayerName());
        }

        final Map<String, BedrockPlayer> foundsPlayers = playerService.getPlayersOrCreate(playerNames);
        assertThat(foundsPlayers).containsOnlyKeys(playerNames);
        for (final Map.Entry<String, BedrockPlayer> entry : foundsPlayers.entrySet()) {
            assertThat(entry.getKey()).isEqualTo(entry.getValue().getName());
        }
    }

    @Test
    void getPlayersOrCreate_pre_created_players() {
        final Set<String> playerNames = new HashSet<>();
        for (int count = 0; 20 > count; count++) {
            final String playerName = generatePlayerName();
            playerNames.add(playerName);
            // Initialize a few players
            if (count % 2 == 0) {
                playerService.getPlayerOrCreate(playerName);
            }
        }

        final Map<String, BedrockPlayer> foundsPlayers = playerService.getPlayersOrCreate(playerNames);
        assertThat(foundsPlayers).containsOnlyKeys(playerNames);
        for (final Map.Entry<String, BedrockPlayer> entry : foundsPlayers.entrySet()) {
            assertThat(entry.getKey()).isEqualTo(entry.getValue().getName());
        }
    }

    @Test
    void getPlayersOrCreate_empty() {
        final Map<String, BedrockPlayer> empty = playerService.getPlayersOrCreate(new HashSet<>());
        assertThat(empty).isEmpty();
    }
}