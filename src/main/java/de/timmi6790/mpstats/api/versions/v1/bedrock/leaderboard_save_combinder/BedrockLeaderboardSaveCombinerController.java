package de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard_save_combinder;

import de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard.BedrockLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.BedrockPlayerService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockPlayer;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.LeaderboardSaveCombinerController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/bedrock/leaderboard")
@Tag(name = "Bedrock - Leaderboard")
public class BedrockLeaderboardSaveCombinerController extends LeaderboardSaveCombinerController<BedrockPlayer, BedrockPlayerService> {
    @Autowired
    public BedrockLeaderboardSaveCombinerController(final BedrockLeaderboardSaveCombinerService leaderboardSaveCombinerService,
                                                    final BedrockLeaderboardService leaderboardService) {
        super(leaderboardSaveCombinerService, leaderboardService);
    }
}
