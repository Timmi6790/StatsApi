package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_cache;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import de.timmi6790.mpstats.api.redis.ByteRedisTemplate;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_cache.models.LeaderboardSaveCache;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Log4j2
public class LeaderboardCacheService<P extends Player> {
    private final String schemaName;

    private final RedisTemplate<String, LeaderboardSaveCache<P>> redisTemplate;
    private final ValueOperations<String, LeaderboardSaveCache<P>> hashOperations;

    protected LeaderboardCacheService(final LettuceConnectionFactory redisConnectionFactory,
                                      final String schemaName,
                                      final Class<P> playerClass) {
        this.schemaName = schemaName;

        final JavaType typeParameter = TypeFactory.defaultInstance().constructParametricType(
                LeaderboardSaveCache.class,
                TypeFactory.defaultInstance().constructType(playerClass)
        );
        this.redisTemplate = new ByteRedisTemplate<>(redisConnectionFactory, typeParameter);
        this.hashOperations = this.redisTemplate.opsForValue();
    }

    protected String getSaveCacheId(final Leaderboard leaderboard) {
        return "SAVE-" + this.schemaName + "-" + leaderboard.repositoryId();
    }

    public void saveLeaderboardEntryPosition(final Leaderboard leaderboard,
                                             final List<LeaderboardEntry<P>> entries,
                                             final LocalDateTime saveTime) {
        log.debug(
                "[{}] Add {}-{}-{} to cache",
                this.schemaName,
                leaderboard.game().gameName(),
                leaderboard.board().boardName(),
                leaderboard.stat().statName()
        );
        this.hashOperations.set(
                this.getSaveCacheId(leaderboard),
                new LeaderboardSaveCache<>(
                        saveTime,
                        entries
                )
        );
    }

    public Optional<LeaderboardSaveCache<P>> retrieveLeaderboardEntryPosition(final Leaderboard leaderboard) {
        return Optional.ofNullable(
                this.hashOperations.get(
                        this.getSaveCacheId(leaderboard)
                )
        );
    }
}
