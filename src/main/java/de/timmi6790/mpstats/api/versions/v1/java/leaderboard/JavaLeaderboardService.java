package de.timmi6790.mpstats.api.versions.v1.java.leaderboard;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.java.board.JavaBoardService;
import de.timmi6790.mpstats.api.versions.v1.java.game.JavaGameService;
import de.timmi6790.mpstats.api.versions.v1.java.stat.JavaStatService;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JavaLeaderboardService extends LeaderboardService {
    @Autowired
    public JavaLeaderboardService(final Jdbi jdbi,
                                  final JavaGameService gameService,
                                  final JavaStatService statService,
                                  final JavaBoardService boardService) {
        super(jdbi, "java", gameService, statService, boardService);
    }
}
