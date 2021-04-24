package de.timmi6790.mpstats.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

@ActiveProfiles("test")
@SpringBootTest(
        classes = {AbstractSpringBootTest.TestApplication.class, AbstractSpringBootTest.TestConfig.class},
        properties = {"spring.main.allow-bean-definition-overriding=true"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
public abstract class AbstractSpringBootTest {
    @Autowired
    protected MockMvc mockMvc;

    @SpringBootApplication
    static class TestApplication {
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public Config config() {
            final Config config = new Config();

            // Sql
            final PostgreSQLContainer<?> postgresSQLContainer = AbstractIntegrationTest.getPostgreSQLContainer();
            final Config.RepositoryConfig repoConfig = config.getRepository();
            repoConfig.setUrl(postgresSQLContainer.getJdbcUrl());
            repoConfig.setPassword(postgresSQLContainer.getPassword());
            repoConfig.setName(postgresSQLContainer.getUsername());

            // Redis
            final GenericContainer<?> redisContainer = AbstractIntegrationTest.getRedisContainer();
            final Config.RedisConfig redisConfig = config.getRedis();
            redisConfig.setHost(redisContainer.getHost());
            redisConfig.setPort(redisContainer.getFirstMappedPort());

            return config;
        }
    }
}
