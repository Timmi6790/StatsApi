package de.timmi6790.mineplex_stats_api.configs;

import de.timmi6790.mineplex_stats_api.Config;
import de.timmi6790.mineplex_stats_api.MineplexStatsApiApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfiguration {
    private JedisPool pool;

    @Bean
    public JedisPool getRedisPool() {
        if (this.pool == null) {
            final Config.RedisConfig config = MineplexStatsApiApplication.getConfig().getRedis();
            this.pool = new JedisPool(
                    new JedisPoolConfig(),
                    config.getHost(),
                    config.getPort(),
                    60,
                    config.getPassword(),
                    config.getDatabase()
            );
        }

        return this.pool;
    }
}
