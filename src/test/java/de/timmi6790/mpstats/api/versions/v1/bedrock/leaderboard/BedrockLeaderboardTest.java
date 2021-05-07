package de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard;

import de.timmi6790.mpstats.api.utilities.bedrock.BedrockServiceGenerator;
import de.timmi6790.mpstats.api.versions.v1.bedrock.board.BedrockBoardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.game.BedrockGameService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.stat.BedrockStatService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.AbstractLeaderboardTest;

public class BedrockLeaderboardTest extends AbstractLeaderboardTest {
    private static final BedrockGameService gameService = BedrockServiceGenerator.generateGameService();
    private static final BedrockStatService statService = BedrockServiceGenerator.generateStatService();
    private static final BedrockBoardService boardService = BedrockServiceGenerator.generateBoardService();

    public BedrockLeaderboardTest() {
        super(
                new BedrockLeaderboardService(
                        BedrockServiceGenerator.getJdbi(),
                        gameService,
                        statService,
                        boardService
                ),
                gameService,
                boardService,
                statService
        );
    }
}