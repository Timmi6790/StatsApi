package de.timmi6790.mpstats.api.versions.v1.common.group.repository.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;

import java.util.List;

public record Group(@JsonIgnore int repositoryId,
                    String groupName,
                    String cleanName,
                    String description,
                    List<String> aliasNames,
                    List<Game> games) {
}
