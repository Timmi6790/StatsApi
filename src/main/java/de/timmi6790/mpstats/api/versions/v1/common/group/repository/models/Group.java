package de.timmi6790.mpstats.api.versions.v1.common.group.repository.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import lombok.Data;

import java.util.Set;

@Data
public class Group {
    @JsonIgnore
    private final int repositoryId;
    private final String groupName;
    private final String cleanName;
    private final String description;
    private final Set<String> aliasNames;
    private final Set<Game> games;
}
