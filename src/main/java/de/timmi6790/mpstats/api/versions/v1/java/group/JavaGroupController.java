package de.timmi6790.mpstats.api.versions.v1.java.group;

import de.timmi6790.mpstats.api.versions.v1.common.group.GroupController;
import de.timmi6790.mpstats.api.versions.v1.java.board.JavaBoardService;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.JavaLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.java.player.JavaPlayerService;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
import de.timmi6790.mpstats.api.versions.v1.java.player_stats.JavaPlayerStatsService;
import de.timmi6790.mpstats.api.versions.v1.java.stat.JavaStatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/java/group")
@Tag(name = "Java - Group")
public class JavaGroupController extends GroupController<JavaPlayer, JavaPlayerService> {
    @Autowired
    public JavaGroupController(final JavaGroupService groupService,
                               final JavaPlayerService playerService,
                               final JavaStatService statService,
                               final JavaBoardService boardService,
                               final JavaLeaderboardService leaderboardService,
                               final JavaPlayerStatsService playerStatsService) {
        super(groupService, playerService, statService, boardService, leaderboardService, playerStatsService);
    }
}
