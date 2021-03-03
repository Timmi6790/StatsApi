package de.timmi6790.mpstats.api;

import lombok.Data;

@Data
public class Config {
    private final RepositoryConfig repository = new RepositoryConfig();
    private final RedisConfig redis = new RedisConfig();
    private final MineplexLeaderboardConfig leaderboard = new MineplexLeaderboardConfig();

    @Data
    public static class RepositoryConfig {
        private String url = "";
        private String name = "";
        private String password = "";
    }

    @Data
    public static class RedisConfig {
        private String host = "";
        private int port = 6379;
        private String password = "";
        private int database = 1;
    }

    @Data
    public static class MineplexLeaderboardConfig {
        private String bedrockUrl = "";
        private String javaUrl = "";
    }
}
