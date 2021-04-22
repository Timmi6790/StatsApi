package de.timmi6790.mpstats.api.versions.v1.common.board.repository.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Set;

public record Board(@JsonIgnore int repositoryId,
                    @JsonIgnore String websiteName,
                    String boardName,
                    String cleanName,
                    int updateTime,
                    Set<String> aliasNames) {
}
