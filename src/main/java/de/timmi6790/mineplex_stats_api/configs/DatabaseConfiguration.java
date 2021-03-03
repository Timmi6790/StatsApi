package de.timmi6790.mineplex_stats_api.configs;

import de.timmi6790.mineplex_stats_api.Config;
import de.timmi6790.mineplex_stats_api.MineplexStatsApiApplication;
import org.jdbi.v3.core.Jdbi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfiguration {
    private Jdbi jdbi;

    @Bean
    public Jdbi jdbi() {
        if (this.jdbi == null) {
            final Config.RepositoryConfig config = MineplexStatsApiApplication.getConfig().getRepository();
            this.jdbi = Jdbi.create(
                    config.getUrl(),
                    config.getName(),
                    config.getPassword()
            );
        }

        return this.jdbi;
    }
}
