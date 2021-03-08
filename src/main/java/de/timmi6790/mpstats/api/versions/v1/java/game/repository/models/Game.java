package de.timmi6790.mpstats.api.versions.v1.java.game.repository.models;

import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

import java.util.Set;

@Data
public class Game {
    @JsonIgnore
    private final int repositoryId;

    private final String gameName;
    private final Set<String> aliasNames;
    private final String categoryName;
    private final String description;
    private final String wikiUrl;
}
