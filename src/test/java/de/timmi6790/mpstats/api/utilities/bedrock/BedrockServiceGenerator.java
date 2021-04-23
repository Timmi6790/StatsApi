package de.timmi6790.mpstats.api.utilities.bedrock;

import de.timmi6790.mpstats.api.AbstractIntegrationTest;
import de.timmi6790.mpstats.api.versions.v1.bedrock.board.BedrockBoardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.game.BedrockGameService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard.BedrockLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.BedrockPlayerService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.postgres.BedrockPlayerPostgresRepository;
import de.timmi6790.mpstats.api.versions.v1.bedrock.stat.BedrockStatService;
import org.jdbi.v3.core.Jdbi;

public class BedrockServiceGenerator {
    public static Jdbi getJdbi() {
        return AbstractIntegrationTest.jdbi();
    }

    public static BedrockGameService generateGameService() {
        return new BedrockGameService(getJdbi());
    }

    public static BedrockBoardService generateBoardService() {
        return new BedrockBoardService(getJdbi());
    }

    public static BedrockStatService generateStatService() {
        return new BedrockStatService(getJdbi());
    }

    public static BedrockLeaderboardService generateLeaderboardService() {
        return new BedrockLeaderboardService(
                getJdbi(),
                generateGameService(),
                generateStatService(),
                generateBoardService()
        );
    }

    public static BedrockPlayerService generatePlayerService() {
        return new BedrockPlayerService(new BedrockPlayerPostgresRepository(getJdbi()));
    }
}
