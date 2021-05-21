package de.timmi6790.mpstats.api.versions.v1.java.player_stats;

import de.timmi6790.mpstats.api.versions.v1.common.player_stats.PlayerStatsService;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.JavaLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard_save_combinder.JavaLeaderboardSaveCombinerService;
import de.timmi6790.mpstats.api.versions.v1.java.player.JavaPlayerService;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JavaPlayerStatsService extends PlayerStatsService<JavaPlayer, JavaPlayerService> {
    @Autowired
    public JavaPlayerStatsService(final JavaLeaderboardSaveCombinerService leaderboardSaveCombinerService,
                                  final JavaLeaderboardService leaderboardService) {
        super(leaderboardSaveCombinerService, leaderboardService);
    }
}
