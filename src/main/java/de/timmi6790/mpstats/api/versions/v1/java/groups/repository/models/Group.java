package de.timmi6790.mpstats.api.versions.v1.java.groups.repository.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.timmi6790.mpstats.api.versions.v1.java.game.repository.models.Game;
import lombok.Data;

import java.util.List;

@Data
public class Group {
    @JsonIgnore
    private final int repositoryId;

    private final String groupName;
    private final String description;
    private final List<String> aliasNames;

    private final List<Game> games;

    public void addAliasNameRepositoryOnly(final String aliasName) {
        this.aliasNames.add(aliasName);
    }

    public void addGameRepositoryOnly(final Game game) {
        this.games.add(game);
    }
}
