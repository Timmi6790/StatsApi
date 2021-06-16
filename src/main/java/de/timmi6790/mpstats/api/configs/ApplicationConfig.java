package de.timmi6790.mpstats.api.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.timmi6790.mpstats.api.Config;
import de.timmi6790.mpstats.api.utilities.FileUtilities;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Paths;

@Configuration
public class ApplicationConfig {
    @Bean
    @SneakyThrows
    public Config config() {
        final File configFile = Paths.get("./configs/config.json").toFile();
        final ObjectMapper objectMapper = new ObjectMapper();
        if (configFile.exists()) {
            return objectMapper.readValue(configFile, Config.class);
        } else {
            final Config config = new Config();
            FileUtilities.saveToFile(objectMapper, configFile, config);
            return config;
        }
    }
}
