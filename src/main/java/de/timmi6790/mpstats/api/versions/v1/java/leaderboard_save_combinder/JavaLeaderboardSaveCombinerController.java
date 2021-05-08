package de.timmi6790.mpstats.api.versions.v1.java.leaderboard_save_combinder;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.LeaderboardSaveCombinerController;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.JavaLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.java.player.JavaPlayerService;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/java/leaderboard")
@Tag(name = "Java - Leaderboard")
public class JavaLeaderboardSaveCombinerController extends LeaderboardSaveCombinerController<JavaPlayer, JavaPlayerService> {
    @Autowired
    public JavaLeaderboardSaveCombinerController(final JavaLeaderboardSaveCombinerService leaderboardSaveCombinerService,
                                                 final JavaLeaderboardService leaderboardService) {
        super(leaderboardSaveCombinerService, leaderboardService);
    }
}
