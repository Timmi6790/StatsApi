package de.timmi6790.mpstats.api.versions.v1.common.player_stats.generator;

import de.timmi6790.mpstats.api.versions.v1.common.player_stats.models.GeneratedPlayerEntry;

import java.util.Collection;

public interface StatGenerator {
    Collection<GeneratedPlayerEntry> generateStats(StatGeneratorData generatorData);
}
