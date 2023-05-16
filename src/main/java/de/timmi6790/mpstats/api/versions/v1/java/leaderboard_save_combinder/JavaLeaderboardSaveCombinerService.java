package de.timmi6790.mpstats.api.versions.v1.java.leaderboard_save_combinder;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.LeaderboardSaveCombinerService;
import de.timmi6790.mpstats.api.versions.v1.java.filter.JavaFilterService;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.JavaLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard_save.JavaLeaderboardSaveService;
import de.timmi6790.mpstats.api.versions.v1.java.player.JavaPlayerService;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JavaLeaderboardSaveCombinerService extends LeaderboardSaveCombinerService<JavaPlayer, JavaPlayerService> {
    @Autowired
    public JavaLeaderboardSaveCombinerService(final JavaLeaderboardService leaderboardService,
                                              final JavaLeaderboardSaveService leaderboardSaveService,
                                              final JavaFilterService filterService) {
        super(leaderboardService, leaderboardSaveService, filterService);
    }
}
