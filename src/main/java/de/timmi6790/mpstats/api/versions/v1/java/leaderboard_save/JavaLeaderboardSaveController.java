package de.timmi6790.mpstats.api.versions.v1.java.leaderboard_save;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.LeaderboardSaveController;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.JavaLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/java/leaderboard")
@Tag(name = "Java - Leaderboard")
public class JavaLeaderboardSaveController extends LeaderboardSaveController<JavaPlayer> {
    @Autowired
    protected JavaLeaderboardSaveController(final JavaLeaderboardService leaderboardService,
                                            final JavaLeaderboardSaveService leaderboardSaveService) {
        super(leaderboardService, leaderboardSaveService);
    }
}
