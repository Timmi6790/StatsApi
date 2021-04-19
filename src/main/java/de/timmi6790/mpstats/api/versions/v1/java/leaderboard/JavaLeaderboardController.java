package de.timmi6790.mpstats.api.versions.v1.java.leaderboard;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardController;
import de.timmi6790.mpstats.api.versions.v1.java.board.JavaBoardService;
import de.timmi6790.mpstats.api.versions.v1.java.game.JavaGameService;
import de.timmi6790.mpstats.api.versions.v1.java.stat.JavaStatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/java/leaderboard")
@Tag(name = "Java - Leaderboard")
public class JavaLeaderboardController extends LeaderboardController {
    @Autowired
    public JavaLeaderboardController(final JavaLeaderboardService leaderboardService,
                                     final JavaGameService gameService,
                                     final JavaStatService statService,
                                     final JavaBoardService boardService) {
        super(leaderboardService, gameService, statService, boardService);
    }
}
