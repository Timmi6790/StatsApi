package de.timmi6790.mpstats.api.versions.v1.java.leaderboard_save;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.LeaderboardSaveService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.models.PlayerData;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.java.player.JavaPlayerService;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaRepositoryPlayer;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JavaLeaderboardSaveService extends LeaderboardSaveService<JavaPlayer, JavaRepositoryPlayer> {
    @Autowired
    public JavaLeaderboardSaveService(final JavaPlayerService playerService, final Jdbi database) {
        super(playerService, database, "java");
    }

    @Override
    protected List<PlayerData> getPlayerData(final List<LeaderboardEntry<JavaPlayer>> leaderboardDataList) {
        final JavaPlayerService playerService = (JavaPlayerService) this.getPlayerService();
        return leaderboardDataList.parallelStream().map(leaderboardData -> {
                    final JavaPlayer player = leaderboardData.getPlayer();
                    final JavaRepositoryPlayer repositoryPlayer = playerService.getPlayerOrCreate(
                            player.getPlayerName(),
                            player.getPlayerUUID()
                    );

                    return new PlayerData(
                            repositoryPlayer.getRepositoryId(),
                            player.getPlayerName(),
                            leaderboardData.getScore()
                    );
                }
        )
                .sorted(Comparator.comparing(PlayerData::score, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
}
