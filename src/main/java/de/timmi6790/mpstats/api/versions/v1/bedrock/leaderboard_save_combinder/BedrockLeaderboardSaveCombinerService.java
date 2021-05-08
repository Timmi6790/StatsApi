package de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard_save_combinder;

import de.timmi6790.mpstats.api.versions.v1.bedrock.filter.BedrockFilterService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard.BedrockLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard_cache.BedrockLeaderboardCacheService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard_request.BedrockLeaderboardRequestService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard_save.BedrockLeaderboardSaveService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.BedrockPlayerService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockPlayer;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.LeaderboardSaveCombinerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BedrockLeaderboardSaveCombinerService extends LeaderboardSaveCombinerService<BedrockPlayer, BedrockPlayerService> {
    @Autowired
    public BedrockLeaderboardSaveCombinerService(final BedrockLeaderboardService leaderboardService,
                                                 final BedrockLeaderboardRequestService leaderboardRequestService,
                                                 final BedrockLeaderboardCacheService leaderboardCacheService,
                                                 final BedrockLeaderboardSaveService leaderboardSaveService,
                                                 final BedrockFilterService filterFilter) {
        super(leaderboardService, leaderboardRequestService, leaderboardCacheService, leaderboardSaveService, filterFilter);
    }
}
