package de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class FilterDuration {
    private final ZonedDateTime start;
    private final ZonedDateTime end;
}
