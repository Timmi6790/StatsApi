package de.timmi6790.mpstats.api.utilities;

import de.timmi6790.mpstats.api.versions.v1.bedrock.player.BedrockPlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.java.player.JavaPlayerService;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerUtilities {
    private static final AtomicInteger PLAYER_ID = new AtomicInteger(0);

    public static String generatePlayerName() {
        return "Player" + PLAYER_ID.incrementAndGet();
    }

    public static UUID generatePlayerUUID() {
        return UUID.randomUUID();
    }

    public static <P extends Player> P generatePlayer(final PlayerService<P> playerService) {
        final String playerName = generatePlayerName();
        if (playerService instanceof JavaPlayerService) {
            return (P) ((JavaPlayerService) playerService).getPlayerOrCreate(playerName, generatePlayerUUID());
        } else if (playerService instanceof BedrockPlayerService) {
            return (P) ((BedrockPlayerService) playerService).getPlayerOrCreate(playerName);
        } else {
            throw new UnsupportedOperationException("This generator only supports JavaPlayerService and BedrockPlayerService");
        }
    }
}
