package de.timmi6790.mpstats.api.versions.v1.java.leaderboard_save;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.LeaderboardSaveService;
import de.timmi6790.mpstats.api.versions.v1.java.player.JavaPlayerService;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JavaLeaderboardSaveService extends LeaderboardSaveService<JavaPlayer> {
    @Autowired
    public JavaLeaderboardSaveService(final JavaPlayerService playerService, final Jdbi database) {
        super(playerService, database, "java");
    }
}
