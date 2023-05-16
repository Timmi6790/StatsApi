package de.timmi6790.mpstats.api;

import lombok.Data;

@Data
public class Config {
    private final RepositoryConfig repository = new RepositoryConfig();

    @Data
    public static class RepositoryConfig {
        private String url = "";
        private String name = "";
        private String password = "";
    }
}
