package de.timmi6790.mineplex_stats_api.configs;

import de.timmi6790.mineplex_stats_api.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfiguration {
    private JedisPool pool;

    @Bean
    public JedisPool getRedisPool(final Config config) {
        if (this.pool == null) {
            final Config.RedisConfig redisConfig = config.getRedis();
            this.pool = new JedisPool(
                    new JedisPoolConfig(),
                    redisConfig.getHost(),
                    redisConfig.getPort(),
                    60,
                    redisConfig.getPassword(),
                    redisConfig.getDatabase()
            );
        }

        return this.pool;
    }
}
