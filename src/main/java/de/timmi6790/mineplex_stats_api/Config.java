package de.timmi6790.mineplex_stats_api;

import lombok.Data;

@Data
public class Config {
    private final DatabaseConfig database = new DatabaseConfig();

    @Data
    public static class DatabaseConfig {
        private String url = "";
        private String name = "";
        private String password = "";
    }
}
