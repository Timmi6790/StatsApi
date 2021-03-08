package de.timmi6790.mpstats.api.versions.v1.java.base.repository.models;

import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

@Data
public class Stat {
    @JsonIgnore
    private final int repositoryId;

    private final String statName;
    private final String websiteName;
    private final String description;
    private final boolean achievement;
}
