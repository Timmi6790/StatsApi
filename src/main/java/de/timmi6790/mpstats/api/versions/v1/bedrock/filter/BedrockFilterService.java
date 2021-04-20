package de.timmi6790.mpstats.api.versions.v1.bedrock.filter;

import de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard.BedrockLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.BedrockPlayerService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockRepositoryPlayer;
import de.timmi6790.mpstats.api.versions.v1.common.filter.FilterService;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BedrockFilterService extends FilterService<BedrockRepositoryPlayer, BedrockPlayerService> {
    @Autowired
    public BedrockFilterService(final Jdbi jdbi,
                                final BedrockPlayerService playerService,
                                final BedrockLeaderboardService leaderboardService) {
        super(playerService, leaderboardService, jdbi, "bedrock");
    }
}
