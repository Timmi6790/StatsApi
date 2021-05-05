package de.timmi6790.mpstats.api.versions.v1.common.player.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Player {
    @JsonIgnore
    private final int repositoryId;
    private String name;
}
