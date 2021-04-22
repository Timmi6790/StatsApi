package de.timmi6790.mpstats.api.versions.v1.common.game.repository.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record GameCategory(@JsonIgnore int repositoryId, String categoryName) {
}
