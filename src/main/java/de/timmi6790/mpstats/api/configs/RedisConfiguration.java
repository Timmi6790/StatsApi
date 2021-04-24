package de.timmi6790.mpstats.api.configs;

import de.timmi6790.mpstats.api.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;


@Configuration
public class RedisConfiguration {
    @Bean
    public LettuceConnectionFactory redisConnectionFactory(final Config config) {
        final Config.RedisConfig redisConfig = config.getRedis();

        final RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(
                redisConfig.getHost(),
                redisConfig.getPort()
        );
        redisStandaloneConfiguration.setPassword(redisConfig.getPassword());
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }
}
