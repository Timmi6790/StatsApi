package de.timmi6790.mineplex_stats_api;

import de.timmi6790.commons.utilities.GsonUtilities;
import de.timmi6790.commons.utilities.ReflectionUtilities;
import lombok.Getter;
import lombok.SneakyThrows;
import org.flywaydb.core.Flyway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class MineplexStatsApiApplication {
    @Getter
    private static Path basePath;

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("DM_EXIT")
    private static void setup() throws IOException {
        // Config
        final Path configFolderPath = Paths.get(basePath + "/configs/");
        Files.createDirectories(configFolderPath);
        final Path configPath = Paths.get(configFolderPath + "/config.json");

        final Config config = Files.exists(configPath) ? getConfig() : new Config();
        final Config newConfig = ReflectionUtilities.deepCopy(config);
        GsonUtilities.saveToJsonIfChanged(configPath, config, newConfig);
    }

    @SneakyThrows
    public static Config getConfig() {
        final Path mainConfigPath = Paths.get(basePath + "/configs/config.json");
        return GsonUtilities.readJsonFile(mainConfigPath, Config.class);
    }

    public static void main(final String[] args) throws IOException {
        basePath = Paths.get(".").toAbsolutePath().normalize();
        setup();

        final Config.DatabaseConfig databaseConfig = getConfig().getDatabase();
        final Flyway flyway = Flyway.configure()
                .dataSource(databaseConfig.getUrl(), databaseConfig.getName(), databaseConfig.getPassword())
                .baselineOnMigrate(true)
                .load();
        flyway.migrate();

        final SpringApplication app = new SpringApplication(MineplexStatsApiApplication.class);
        app.run(args);
    }
}
