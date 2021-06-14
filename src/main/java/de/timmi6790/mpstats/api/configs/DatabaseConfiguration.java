package de.timmi6790.mpstats.api.configs;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.timmi6790.mpstats.api.Config;
import io.micrometer.core.instrument.MeterRegistry;
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
    public Jdbi jdbi(final Config config, final MeterRegistry registry) {
        final Config.RepositoryConfig databaseConfig = config.getRepository();

        setupDatabase(
                databaseConfig.getUrl(),
                databaseConfig.getName(),
                databaseConfig.getPassword()
        );

        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(databaseConfig.getUrl());
        hikariConfig.setUsername(databaseConfig.getName());
        hikariConfig.setPassword(databaseConfig.getPassword());
        
        hikariConfig.addDataSourceProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
        // I'm not sure if those properties even work for postgres
        hikariConfig.addDataSourceProperty("cachePrepStmts", true);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        hikariConfig.addDataSourceProperty("useServerPrepStmts", true);

        // Register metrics
        hikariConfig.setMetricRegistry(registry);

        return Jdbi.create(new HikariDataSource(hikariConfig));
    }
}
