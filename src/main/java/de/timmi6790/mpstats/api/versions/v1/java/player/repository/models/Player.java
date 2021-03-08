package de.timmi6790.mpstats.api.versions.v1.java.player.repository.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.UUID;

@Data
public class Player {
    @JsonIgnore
    private final int repositoryId;

    private String playerName;
    private UUID playerUUID;
}
