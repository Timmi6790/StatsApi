package de.timmi6790.mpstats.api.versions.v1.java.leaderboard_cache;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_cache.LeaderboardCacheService;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Service;

@Service
public class JavaLeaderboardCacheService extends LeaderboardCacheService<JavaPlayer> {
    @Autowired
    public JavaLeaderboardCacheService(final LettuceConnectionFactory redisConnectionFactory) {
        super(redisConnectionFactory, "J", JavaPlayer.class);
    }
}
