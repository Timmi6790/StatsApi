package de.timmi6790.mpstats.api.versions.v1.common.game.repository.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.Set;

public record Game(@JsonIgnore int repositoryId,
                   @JsonIgnore String websiteName,
                   String gameName,
                   String cleanName,
                   Set<String> aliasNames,
                   String categoryName,
                   @Nullable String description,
                   @Nullable String wikiUrl) {
}
