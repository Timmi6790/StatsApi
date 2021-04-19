package de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard;

import de.timmi6790.mpstats.api.versions.v1.bedrock.board.BedrockBoardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.game.BedrockGameService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.stat.BedrockStatService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/bedrock/leaderboard")
@Tag(name = "Bedrock - Leaderboard")
public class BedrockLeaderboardController extends LeaderboardController {
    @Autowired
    public BedrockLeaderboardController(final BedrockLeaderboardService leaderboardService,
                                        final BedrockGameService gameService,
                                        final BedrockStatService statService,
                                        final BedrockBoardService boardService) {
        super(leaderboardService, gameService, statService, boardService);
    }
}
