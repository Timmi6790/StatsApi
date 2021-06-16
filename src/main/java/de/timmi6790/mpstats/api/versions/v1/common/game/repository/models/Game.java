package de.timmi6790.mpstats.api.versions.v1.common.game.repository.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

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
