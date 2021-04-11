package de.timmi6790.mpstats.api.versions.v1.java.groups.repository.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.timmi6790.mpstats.api.versions.v1.common.game.models.Game;
import lombok.Data;

import java.util.List;

@Data
public class Group {
    @JsonIgnore
    private final int repositoryId;

    private final String groupName;
    private final String cleanName;
    private final String description;
    private final List<String> aliasNames;

    private final List<Game> games;
}
