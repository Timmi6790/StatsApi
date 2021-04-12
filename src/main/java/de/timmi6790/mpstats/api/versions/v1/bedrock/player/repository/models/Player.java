package de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Player {
    @JsonIgnore
    private final int repositoryId;

    private String playerName;
}
