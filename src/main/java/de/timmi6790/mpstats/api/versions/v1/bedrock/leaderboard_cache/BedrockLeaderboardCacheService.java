package de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard_cache;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_cache.LeaderboardCacheService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Service;

@Service
public class BedrockLeaderboardCacheService extends LeaderboardCacheService<Player> {
    @Autowired
    public BedrockLeaderboardCacheService(final LettuceConnectionFactory redisConnectionFactory) {
        super(redisConnectionFactory, "B", Player.class);
    }
}
