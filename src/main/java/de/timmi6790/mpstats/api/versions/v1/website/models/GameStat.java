package de.timmi6790.mpstats.api.versions.v1.website.models;

import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class GameStat {
    private final Map<Stat, Long> stats = new HashMap<>();
    private final Map<String, Long> otherStats = new HashMap<>();

    public void addStat(final Stat stat, final long value) {
        this.stats.put(stat, value);
    }

    public void addStat(final String cleanStatName, final long value) {
        this.otherStats.put(cleanStatName, value);
    }

    public void merge(final GameStat gameStat) {
        for (final Map.Entry<Stat, Long> statEntry : gameStat.getStats().entrySet()) {
            this.stats.put(statEntry.getKey(), statEntry.getValue());
        }
        
        for (Map.Entry<String, Long> statEntry : gameStat.getOtherStats().entrySet()) {
            this.otherStats.put(statEntry.getKey(), statEntry.getValue());
        }
    }
}
