package de.timmi6790.mpstats.api.versions.v1.java.game.repository.models;

import edu.umd.cs.findbugs.annotations.Nullable;
import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

import java.util.Set;

@Data
public class Game {
    @JsonIgnore
    private final int repositoryId;
    @JsonIgnore
    private final String websiteName;

    private final String gameName;
    private final String cleanName;
    private final Set<String> aliasNames;
    private final String categoryName;
    @Nullable
    private final String description;
    @Nullable
    private final String wikiUrl;
}
