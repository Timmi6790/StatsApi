package de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard;

import de.timmi6790.mpstats.api.versions.v1.bedrock.board.BedrockBoardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.game.BedrockGameService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.stat.BedrockStatService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BedrockLeaderboardService extends LeaderboardService {
    @Autowired
    public BedrockLeaderboardService(final Jdbi jdbi,
                                     final BedrockGameService gameService,
                                     final BedrockStatService statService,
                                     final BedrockBoardService boardService) {
        super(jdbi, "bedrock", gameService, statService, boardService);
    }
}
