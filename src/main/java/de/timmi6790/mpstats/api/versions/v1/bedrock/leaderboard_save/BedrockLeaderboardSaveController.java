package de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard_save;

import de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard.BedrockLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockPlayer;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.LeaderboardSaveController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/bedrock/leaderboard")
@Tag(name = "Bedrock - Leaderboard")
public class BedrockLeaderboardSaveController extends LeaderboardSaveController<BedrockPlayer> {
    @Autowired
    protected BedrockLeaderboardSaveController(final BedrockLeaderboardService leaderboardService,
                                               final BedrockLeaderboardSaveService leaderboardSaveService) {
        super(leaderboardService, leaderboardSaveService);
    }
}
