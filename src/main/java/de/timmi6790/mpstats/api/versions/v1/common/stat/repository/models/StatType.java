package de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models;

public enum StatType {
    NUMBER,
    TIME_IN_SECONDS;

    public static StatType getDefault() {
        return NUMBER;
    }
}
