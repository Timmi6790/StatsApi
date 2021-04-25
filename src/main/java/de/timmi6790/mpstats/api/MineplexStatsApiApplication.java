package de.timmi6790.mpstats.api;

import de.timmi6790.commons.utilities.GsonUtilities;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@Log4j2
public class MineplexStatsApiApplication {
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("DM_EXIT")
    private static boolean setup() throws IOException {
        // Config
        final Path configFolderPath = Paths.get("./configs/");
        Files.createDirectories(configFolderPath);
        final Path configPath = Paths.get(configFolderPath + "/config.json");

        final boolean firstInnit = !Files.exists(configPath);
        final Config config = firstInnit ? new Config() : getConfig();

        GsonUtilities.saveToJson(configPath, config);
        if (firstInnit) {
            log.info("Created main config file.");
            return false;
        }

        return true;
    }

    @SneakyThrows
    public static Config getConfig() {
        final Path mainConfigPath = Paths.get("./configs/config.json");
        return GsonUtilities.readJsonFile(mainConfigPath, Config.class);
    }

    public static void main(final String[] args) throws IOException {
        if (!setup()) {
            return;
        }

        final SpringApplication app = new SpringApplication(MineplexStatsApiApplication.class);
        app.run(args);
    }
}
