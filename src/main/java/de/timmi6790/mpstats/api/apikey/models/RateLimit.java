package de.timmi6790.mpstats.api.apikey.models;

import lombok.Data;

@Data
public class RateLimit {
    private final int daily;
    private final int minute;
}
