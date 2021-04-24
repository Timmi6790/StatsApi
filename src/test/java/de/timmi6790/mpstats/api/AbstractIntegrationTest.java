package de.timmi6790.mpstats.api;

import de.timmi6790.mpstats.api.configs.DatabaseConfiguration;
import org.jdbi.v3.core.Jdbi;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class AbstractIntegrationTest {
    // TODO: Add redis
    @Container
    private static final PostgreSQLContainer<?> POSTGRES_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName
                    .parse("timescale/timescaledb:2.1.1-pg13")
                    .asCompatibleSubstituteFor("postgres")
    );

    @Container
    private static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:6.2.2"))
            .withExposedPorts(6379);

    static {
        // Postgres
        POSTGRES_SQL_CONTAINER.start();
        try {
            DatabaseConfiguration.setupDatabase(
                    POSTGRES_SQL_CONTAINER.getJdbcUrl(),
                    POSTGRES_SQL_CONTAINER.getUsername(),
                    POSTGRES_SQL_CONTAINER.getPassword()
            );
        } catch (final Exception e) {
            e.printStackTrace();
            throw e;
        }

        // Redis
        REDIS_CONTAINER.start();
    }

    public static PostgreSQLContainer<?> getPostgreSQLContainer() {
        return POSTGRES_SQL_CONTAINER;
    }

    public static Jdbi jdbi() {
        return Jdbi.create(
                POSTGRES_SQL_CONTAINER.getJdbcUrl(),
                POSTGRES_SQL_CONTAINER.getUsername(),
                POSTGRES_SQL_CONTAINER.getPassword()
        );
    }

    public static GenericContainer<?> getRedisContainer() {
        return REDIS_CONTAINER;
    }

    public static LettuceConnectionFactory lettuceConnectionFactory() {
        final RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(
                REDIS_CONTAINER.getHost(),
                REDIS_CONTAINER.getFirstMappedPort()
        );
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }
}