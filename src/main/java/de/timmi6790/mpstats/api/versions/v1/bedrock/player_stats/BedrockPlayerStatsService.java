package de.timmi6790.mpstats.api.versions.v1.bedrock.player_stats;

import de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard.BedrockLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard_save_combinder.BedrockLeaderboardSaveCombinerService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.BedrockPlayerService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockPlayer;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.PlayerStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BedrockPlayerStatsService extends PlayerStatsService<BedrockPlayer, BedrockPlayerService> {
    @Autowired
    public BedrockPlayerStatsService(final BedrockLeaderboardSaveCombinerService leaderboardSaveCombinerService,
                                     final BedrockLeaderboardService leaderboardService) {
        super(leaderboardSaveCombinerService, leaderboardService);
    }
}
