package de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard_save;

import de.timmi6790.mpstats.api.versions.v1.bedrock.player.BedrockPlayerService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockPlayer;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockRepositoryPlayer;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.LeaderboardSaveService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.models.PlayerData;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BedrockLeaderboardSaveService extends LeaderboardSaveService<BedrockPlayer, BedrockRepositoryPlayer> {
    @Autowired
    public BedrockLeaderboardSaveService(final BedrockPlayerService playerService, final Jdbi database) {
        super(playerService, database, "bedrock");
    }

    @Override
    protected List<PlayerData> getPlayerData(final List<LeaderboardEntry<BedrockPlayer>> leaderboardDataList) {
        final BedrockPlayerService playerService = (BedrockPlayerService) this.getPlayerService();

        return leaderboardDataList.parallelStream().map(leaderboardData -> {
                    final Player player = leaderboardData.getPlayer();
                    final BedrockRepositoryPlayer repositoryPlayer = playerService.getPlayerOrCreate(player.getPlayerName());

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
