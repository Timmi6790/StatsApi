package de.timmi6790.mpstats.api.versions.v1.java.base.repository.models;

import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

import java.util.Set;

@Data
public class Board {
    @JsonIgnore
    private final int repositoryId;
    
    private final String boardName;
    private final Set<String> aliasNames;
}
