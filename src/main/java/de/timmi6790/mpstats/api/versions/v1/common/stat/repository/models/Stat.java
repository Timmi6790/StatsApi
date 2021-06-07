package de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Set;

@Data
public class Stat {
    @JsonIgnore
    private final int repositoryId;
    @JsonIgnore
    private final String websiteName;
    private final String statName;
    private final String cleanName;
    private final String description;
    private final boolean achievement;
    private final Set<String> aliasNames;
    private final int sortingPriority;
    private final StatType type;
}
