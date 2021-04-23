package de.timmi6790.mpstats.api.utilities.java;

import de.timmi6790.mpstats.api.AbstractIntegrationTest;
import de.timmi6790.mpstats.api.versions.v1.java.board.JavaBoardService;
import de.timmi6790.mpstats.api.versions.v1.java.game.JavaGameService;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.JavaLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.java.player.JavaPlayerService;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.postgres.JavaPlayerPostgresRepository;
import de.timmi6790.mpstats.api.versions.v1.java.stat.JavaStatService;
import org.jdbi.v3.core.Jdbi;

public class JavaServiceGenerator {
    public static Jdbi getJdbi() {
        return AbstractIntegrationTest.jdbi();
    }

    public static JavaGameService generateGameService() {
        return new JavaGameService(getJdbi());
    }

    public static JavaBoardService generateBoardService() {
        return new JavaBoardService(getJdbi());
    }

    public static JavaStatService generateStatService() {
        return new JavaStatService(getJdbi());
    }

    public static JavaLeaderboardService generateLeaderboardService() {
        return new JavaLeaderboardService(
                getJdbi(),
                generateGameService(),
                generateStatService(),
                generateBoardService()
        );
    }

    public static JavaPlayerService generatePlayerService() {
        return new JavaPlayerService(new JavaPlayerPostgresRepository(getJdbi()));
    }
}
