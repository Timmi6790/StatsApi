package de.timmi6790.mpstats.api.configs;

import de.timmi6790.mpstats.api.Config;
import io.lettuce.core.metrics.MicrometerCommandLatencyRecorder;
import io.lettuce.core.metrics.MicrometerOptions;
import io.lettuce.core.resource.ClientResources;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;


@Configuration
public class RedisConfiguration {
    @Bean
    public LettuceConnectionFactory redisConnectionFactory(final Config config, final MeterRegistry registry) {
        final Config.RedisConfig redisConfig = config.getRedis();

        // Metrics
        final MicrometerOptions options = MicrometerOptions.create();
        final ClientResources resources = ClientResources
                .builder()
                .commandLatencyRecorder(new MicrometerCommandLatencyRecorder(registry, options))
                .build();

        final LettuceClientConfiguration configuration = LettuceClientConfiguration
                .builder()
                .clientResources(resources)
                .build();

        // Config
        final RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(
                redisConfig.getHost(),
                redisConfig.getPort()
        );
        redisStandaloneConfiguration.setPassword(redisConfig.getPassword());
        redisStandaloneConfiguration.setDatabase(redisConfig.getDatabase());

        return new LettuceConnectionFactory(redisStandaloneConfiguration, configuration);
    }
}
