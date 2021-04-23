package de.timmi6790.mpstats.api.versions.v1.bedrock.player;

import de.timmi6790.mpstats.api.utilities.bedrock.BedrockServiceGenerator;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.BedrockPlayerRepository;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockRepositoryPlayer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static de.timmi6790.mpstats.api.utilities.PlayerUtilities.generatePlayerName;
import static org.assertj.core.api.Assertions.assertThat;

class BedrockPlayerServiceTest {
    private static BedrockPlayerRepository bedrockPlayerRepository;
    private static BedrockPlayerService bedrockPlayerService;

    @BeforeAll
    static void setUp() {
        bedrockPlayerService = BedrockServiceGenerator.generatePlayerService();
        bedrockPlayerRepository = bedrockPlayerService.getPlayerRepository();
    }

    @Test
    void hasPlayer() {
        final String playerName = generatePlayerName();

        final boolean playerNotFound = bedrockPlayerService.hasPlayer(playerName);
        assertThat(playerNotFound).isFalse();

        // Insert player
        bedrockPlayerService.getPlayerOrCreate(playerName);

        final boolean playerFound = bedrockPlayerService.hasPlayer(playerName);
        assertThat(playerFound).isTrue();
    }

    @Test
    void hasPlayer_ignore_case() {
        final String playerName = generatePlayerName();

        // Insert player
        bedrockPlayerService.getPlayerOrCreate(playerName);

        // Uppercase check
        final boolean playerUpper = bedrockPlayerService.hasPlayer(playerName.toUpperCase());
        assertThat(playerUpper).isTrue();

        // Lowercase check
        final boolean playerLower = bedrockPlayerService.hasPlayer(playerName.toLowerCase());
        assertThat(playerLower).isTrue();
    }

    @Test
    void getPlayer() {
        final String playerName = generatePlayerName();

        final Optional<BedrockRepositoryPlayer> playerNotFound = bedrockPlayerService.getPlayer(playerName);
        assertThat(playerNotFound).isNotPresent();

        // Insert player
        bedrockPlayerService.getPlayerOrCreate(playerName);

        final Optional<BedrockRepositoryPlayer> playerFound = bedrockPlayerService.getPlayer(playerName);
        assertThat(playerFound).isPresent();
    }

    @Test
    void getPlayer_by_id() {
        final String playerName = generatePlayerName();

        // Insert player
        final BedrockRepositoryPlayer repositoryPlayer = bedrockPlayerService.getPlayerOrCreate(playerName);

        final Optional<BedrockRepositoryPlayer> playerFound = bedrockPlayerService.getPlayer(repositoryPlayer.getRepositoryId());
        assertThat(playerFound)
                .isPresent()
                .contains(repositoryPlayer);
    }

    @Test
    void getPlayer_ignore_case() {
        final String playerName = generatePlayerName();

        // Insert player
        bedrockPlayerService.getPlayerOrCreate(playerName);

        // Uppercase check
        final Optional<BedrockRepositoryPlayer> playerUpper = bedrockPlayerService.getPlayer(playerName.toUpperCase());
        assertThat(playerUpper).isPresent();

        // Lowercase check
        final Optional<BedrockRepositoryPlayer> playerLower = bedrockPlayerService.getPlayer(playerName.toLowerCase());
        assertThat(playerLower).isPresent();
    }

    @Test
    void getPlayerOrCreate() {
        final String playerName = generatePlayerName();

        final Optional<BedrockRepositoryPlayer> playerNotFound = bedrockPlayerService.getPlayer(playerName);
        assertThat(playerNotFound).isNotPresent();

        final BedrockRepositoryPlayer player = bedrockPlayerService.getPlayerOrCreate(playerName);
        assertThat(player.getPlayerName()).isEqualTo(playerName);

        // Cache check
        final BedrockRepositoryPlayer playerCache = bedrockPlayerService.getPlayerOrCreate(playerName);
        assertThat(player).isEqualTo(playerCache);

        // No cache check
        final Optional<BedrockRepositoryPlayer> playerNoCache = bedrockPlayerRepository.getPlayer(playerName);
        assertThat(playerNoCache)
                .isPresent()
                .contains(player);
    }
}