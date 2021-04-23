package de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard_save;

import com.google.common.collect.Lists;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.BedrockPlayerService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockRepositoryPlayer;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.LeaderboardSaveService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.models.PlayerData;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BedrockLeaderboardSaveService extends LeaderboardSaveService<Player, BedrockRepositoryPlayer> {
    @Autowired
    public BedrockLeaderboardSaveService(final BedrockPlayerService playerService, final Jdbi database) {
        super(playerService, database, "bedrock");
    }

    @Override
    protected List<PlayerData> getPlayerData(final List<LeaderboardEntry<Player>> leaderboardDataList) {
        final BedrockPlayerService playerService = (BedrockPlayerService) this.getPlayerService();

        final List<PlayerData> data = Lists.newArrayListWithCapacity(leaderboardDataList.size());
        for (final LeaderboardEntry<Player> leaderboardData : leaderboardDataList) {
            final Player player = leaderboardData.getPlayer();
            final BedrockRepositoryPlayer repositoryPlayer = playerService.getPlayerOrCreate(player.getPlayerName());

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
