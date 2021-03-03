package de.timmi6790.mineplex_stats_api;

import de.timmi6790.commons.utilities.GsonUtilities;
import de.timmi6790.commons.utilities.ReflectionUtilities;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.flywaydb.core.Flyway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
@Log4j2
@EnableScheduling
public class MineplexStatsApiApplication {
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("DM_EXIT")
    private static boolean setup() throws IOException {
        // Config
        final Path configFolderPath = Paths.get("./configs/");
        Files.createDirectories(configFolderPath);
        final Path configPath = Paths.get(configFolderPath + "/config.json");

        final boolean firstInnit = !Files.exists(configPath);

        final Config config = firstInnit ? new Config() : getConfig();
        final Config newConfig = ReflectionUtilities.deepCopy(config);

        GsonUtilities.saveToJsonIfChanged(configPath, config, newConfig);
        if (firstInnit) {
            GsonUtilities.saveToJson(configPath, newConfig);
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

        final Config.RepositoryConfig repositoryConfig = getConfig().getRepository();
        final Flyway flyway = Flyway.configure()
                .dataSource(repositoryConfig.getUrl(), repositoryConfig.getName(), repositoryConfig.getPassword())
                .baselineOnMigrate(true)
                .load();
        flyway.migrate();

        final SpringApplication app = new SpringApplication(MineplexStatsApiApplication.class);
        app.run(args);
    }
}
