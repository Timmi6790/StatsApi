package de.timmi6790.mpstats.api.versions.v1.java.cache.redis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.timmi6790.mpstats.api.versions.v1.java.cache.JavaCache;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.models.JavaLeaderboardModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

@Service
public class RedisJavaCache implements JavaCache {
    private final JedisPool redisPool;

    private final Gson gson = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .create();

    @Autowired
    public RedisJavaCache(final JedisPool redisPool) {
        this.redisPool = redisPool;
    }

    private String getLeaderboardKey(final int leaderboardId, final boolean filtered) {
        return (filtered ? "JAVA-LB-" : "JAVA-ULB-") + leaderboardId;
    }

    @Override
    public void storeLeaderboard(final int leaderboardId,
                                 final boolean filtered,
                                 final List<JavaLeaderboardModel> leaderboardModels) {
        try (final Jedis redis = this.redisPool.getResource()) {
            redis.set(
                    this.getLeaderboardKey(leaderboardId, filtered),
                    this.gson.toJson(leaderboardModels)
            );
        }
    }

    @Override
    public List<JavaLeaderboardModel> retrieveLeaderboard(final int leaderboardId, final boolean filtered) {
        try (final Jedis redis = this.redisPool.getResource()) {
            final String json = redis.get(this.getLeaderboardKey(leaderboardId, filtered));
            return (List<JavaLeaderboardModel>) this.gson.fromJson(json, List.class);
        }
    }
}
