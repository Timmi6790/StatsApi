package de.timmi6790.mpstats.api;

import lombok.Data;

@Data
public class Config {
    private final RepositoryConfig repository;

    @Data
    public static class RepositoryConfig {
        private final String url;
        private final String name;
        private final String password;
    }
}
