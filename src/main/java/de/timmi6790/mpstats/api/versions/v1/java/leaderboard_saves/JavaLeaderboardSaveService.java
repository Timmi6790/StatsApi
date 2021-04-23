package de.timmi6790.mpstats.api.versions.v1.java.leaderboard_saves;

import com.google.common.collect.Lists;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_saves.LeaderboardSaveService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_saves.models.PlayerData;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.java.player.JavaPlayerService;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaRepositoryPlayer;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JavaLeaderboardSaveService extends LeaderboardSaveService<JavaPlayer, JavaRepositoryPlayer> {
    @Autowired
    public JavaLeaderboardSaveService(final JavaPlayerService playerService, final Jdbi database) {
        super(playerService, database, "java");
    }

    @Override
    protected List<PlayerData> getPlayerData(final List<LeaderboardEntry<JavaPlayer>> leaderboardDataList) {
        final JavaPlayerService playerService = (JavaPlayerService) this.getPlayerService();

        final List<PlayerData> data = Lists.newArrayListWithCapacity(leaderboardDataList.size());
        for (final LeaderboardEntry<JavaPlayer> leaderboardData : leaderboardDataList) {
            final JavaPlayer player = leaderboardData.getPlayer();
            final JavaRepositoryPlayer repositoryPlayer = playerService.getPlayerOrCreate(player.getPlayerName(), player.getPlayerUUID());

            data.add(
                    new PlayerData(
                            repositoryPlayer.getRepositoryId(),
                            leaderboardData.getScore()
                    )
            );
        }
        return data;
    }
}
