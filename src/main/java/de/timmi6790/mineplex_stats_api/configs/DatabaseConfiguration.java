package de.timmi6790.mineplex_stats_api.configs;

import de.timmi6790.mineplex_stats_api.Config;
import de.timmi6790.mineplex_stats_api.MineplexStatsApiApplication;
import org.jdbi.v3.core.Jdbi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfiguration {
    @Bean
    public Jdbi jdbi() {
        final Config config = MineplexStatsApiApplication.getConfig();
        return Jdbi.create(config.getDatabase().getUrl(), config.getDatabase().getName(), config.getDatabase().getPassword());
    }
}
