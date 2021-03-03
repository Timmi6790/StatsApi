package de.timmi6790.mpstats.api.configs;

import de.timmi6790.mpstats.api.Config;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfiguration {
    public static void setupDatabase(final String url, final String name, final String password) {
        Flyway.configure()
                .dataSource(
                        url,
                        name,
                        password
                ).load()
                .migrate();
    }

    @Bean
    public Jdbi jdbi(final Config config) {
        final Config.RepositoryConfig databaseConfig = config.getRepository();

        setupDatabase(
                databaseConfig.getUrl(),
                databaseConfig.getName(),
                databaseConfig.getPassword()
        );
        return Jdbi.create(
                databaseConfig.getUrl(),
                databaseConfig.getName(),
                databaseConfig.getPassword()
        );
    }
}
