package de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Set;

public record Stat(@JsonIgnore int repositoryId,
                   @JsonIgnore String websiteName,
                   String statName,
                   String cleanName,
                   String description,
                   boolean achievement,
                   Set<String> aliasNames) {
}
