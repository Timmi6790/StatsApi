package de.timmi6790.mpstats.api.configs;

import de.timmi6790.commons.utilities.GsonUtilities;
import de.timmi6790.mpstats.api.Config;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class ApplicationConfig {
    @Bean
    @SneakyThrows
    public Config config() {
        final Path mainConfigPath = Paths.get("./configs/config.json");
        return GsonUtilities.readJsonFile(mainConfigPath, Config.class);
    }
}
