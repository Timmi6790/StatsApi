package de.timmi6790.mpstats.api.versions.v1.common.filter.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Reason {
    GLITCHED,
    GIVEN,
    BOOSTED,
    HACKED,
    SUSPECTED_BOOSTED,
    SUSPECTED_HACKED
}
