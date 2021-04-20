package de.timmi6790.mpstats.api.versions.v1.java.filter;

import de.timmi6790.mpstats.api.versions.v1.common.filter.FilterService;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.JavaLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.java.player.JavaPlayerService;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaRepositoryPlayer;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JavaFilterService extends FilterService<JavaRepositoryPlayer, JavaPlayerService> {
    @Autowired
    public JavaFilterService(final Jdbi jdbi,
                             final JavaPlayerService playerService,
                             final JavaLeaderboardService leaderboardService) {
        super(playerService, leaderboardService, jdbi, "java");
    }
}
