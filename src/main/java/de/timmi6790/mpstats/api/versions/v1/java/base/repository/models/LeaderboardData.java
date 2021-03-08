package de.timmi6790.mpstats.api.versions.v1.java.base.repository.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class LeaderboardData {
    @JsonIgnore
    private final int repositoryId;

    private final Game game;
    private final Stat stat;
    private final Board board;
    private final boolean deprecated;
    private final long lastUpdate;
}
