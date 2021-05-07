package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_cache;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import de.timmi6790.mpstats.api.redis.ByteRedisTemplate;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardSave;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Log4j2
public abstract class LeaderboardCacheService<P extends Player> {
    private final String schemaName;

    private final RedisTemplate<String, LeaderboardSave<P>> redisTemplate;
    private final ValueOperations<String, LeaderboardSave<P>> hashOperations;

    protected LeaderboardCacheService(final LettuceConnectionFactory redisConnectionFactory,
                                      final String schemaName,
                                      final Class<P> playerClass) {
        this.schemaName = schemaName;

        final JavaType typeParameter = TypeFactory.defaultInstance().constructParametricType(
                LeaderboardSave.class,
                TypeFactory.defaultInstance().constructType(playerClass)
        );
        this.redisTemplate = new ByteRedisTemplate<>(redisConnectionFactory, typeParameter);
        this.hashOperations = this.redisTemplate.opsForValue();
    }

    protected String getSaveCacheId(final Leaderboard leaderboard) {
        return "SAVE-" + this.schemaName + "-" + leaderboard.getRepositoryId();
    }

    public void saveLeaderboardEntryPosition(final Leaderboard leaderboard,
                                             final List<LeaderboardEntry<P>> entries,
                                             final ZonedDateTime saveTime) {
        log.info(
                "[{}] Add {}-{}-{} to cache",
                this.schemaName,
                leaderboard.getGame().getGameName(),
                leaderboard.getBoard().getBoardName(),
                leaderboard.getStat().getStatName()
        );
        leaderboard.setLastCacheSaveTime(saveTime);
        
        // Let them expire after a long time to prevent zombie data
        this.hashOperations.set(
                this.getSaveCacheId(leaderboard),
                new LeaderboardSave<>(
                        saveTime,
                        entries
                ),
                10, TimeUnit.DAYS
        );
    }

    public Optional<LeaderboardSave<P>> retrieveLeaderboardEntryPosition(final Leaderboard leaderboard) {
        return Optional.ofNullable(
                this.hashOperations.get(
                        this.getSaveCacheId(leaderboard)
                )
        );
    }
}
