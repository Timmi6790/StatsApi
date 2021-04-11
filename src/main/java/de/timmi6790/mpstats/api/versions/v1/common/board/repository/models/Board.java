package de.timmi6790.mpstats.api.versions.v1.common.board.repository.models;

import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

import java.util.Set;

@Data
public class Board {
    @JsonIgnore
    private final int repositoryId;
    @JsonIgnore
    private final String websiteName;

    private final String boardName;
    private final String cleanName;
    private final int updateTime;
    private final Set<String> aliasNames;
}
