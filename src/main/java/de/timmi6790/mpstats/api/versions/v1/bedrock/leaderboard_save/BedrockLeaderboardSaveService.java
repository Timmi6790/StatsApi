package de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard_save;

import de.timmi6790.mpstats.api.versions.v1.bedrock.player.BedrockPlayerService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockPlayer;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.LeaderboardSaveService;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BedrockLeaderboardSaveService extends LeaderboardSaveService<BedrockPlayer> {
    @Autowired
    public BedrockLeaderboardSaveService(final BedrockPlayerService playerService, final Jdbi database) {
        super(playerService, database, "bedrock");
    }
}
