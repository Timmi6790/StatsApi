package de.timmi6790.mpstats.api.configs;

import de.timmi6790.mpstats.api.Config;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class ApplicationConfig {
    @Autowired
    private Environment env;

    @Bean
    @SneakyThrows
    public Config config() {
        final Config.RepositoryConfig repositoryConfig = new Config.RepositoryConfig(
                env.getProperty("REPOSITORY.URL"),
                env.getProperty("REPOSITORY.NAME"),
                env.getProperty("REPOSITORY.PASSWORD")
        );

        return new Config(repositoryConfig);
    }
}
