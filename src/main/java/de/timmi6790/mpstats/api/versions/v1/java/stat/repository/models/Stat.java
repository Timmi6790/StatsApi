package de.timmi6790.mpstats.api.versions.v1.java.stat.repository.models;

import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

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
}
