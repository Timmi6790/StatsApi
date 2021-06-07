package de.timmi6790.mpstats.api.versions.v1.common.stat;

import com.google.common.util.concurrent.Striped;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.StatRepository;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.StatType;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.postgres.StatPostgresRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jdbi.v3.core.Jdbi;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

@Log4j2
// TODO: Add test
public class StatService {
    @Getter(AccessLevel.PROTECTED)
    private final StatRepository statRepository;

    private final Striped<Lock> statLock = Striped.lock(32);
    private final Map<String, Stat> stats;
    private final Map<String, String> aliasNames;

    private final String schema;

    public StatService(final Jdbi jdbi, final String schema) {
        this.schema = schema;
        this.statRepository = new StatPostgresRepository(jdbi, schema);

        // Load existing stats from repository
        log.info("[{}] Load stats from repository", schema);
        final List<Stat> existingStats = this.statRepository.getStats();
        this.stats = new LinkedCaseInsensitiveMap<>(existingStats.size());
        this.aliasNames = new LinkedCaseInsensitiveMap<>();
        for (final Stat stat : existingStats) {
            this.stats.put(stat.getStatName(), stat);

            for (final String aliasName : stat.getAliasNames()) {
                this.aliasNames.put(aliasName, stat.getStatName());
            }
        }
        log.info("[{}] Loaded {} stats from repository", schema, this.stats.size());
        log.info("[{}] Loaded {} stat alias names from repository", schema, this.aliasNames.size());

        // Insert all stat types
        this.statRepository.addTypes(StatType.values());
    }

    private Lock getStatLock(final String statName) {
        return this.statLock.get(statName.toLowerCase());
    }

    private String getStatName(final String statName) {
        return this.aliasNames.getOrDefault(statName, statName);
    }

    public boolean hasStat(final String statName) {
        return this.stats.containsKey(this.getStatName(statName));
    }

    public List<Stat> getStats() {
        return new ArrayList<>(this.stats.values());
    }

    public Optional<Stat> getStat(final String statName) {
        return Optional.ofNullable(this.stats.get(this.getStatName(statName)));
    }

    public Stat getStatOrCreate(final String websiteName,
                                final String statName,
                                final String cleanName,
                                final boolean isAchievement) {
        final Lock lock = this.getStatLock(statName);
        lock.lock();
        try {
            if (this.hasStat(statName)) {
                return this.getStat(statName).orElseThrow(RuntimeException::new);
            }

            final Stat stat = this.statRepository.createStat(websiteName, statName, cleanName, isAchievement);
            this.stats.put(stat.getStatName(), stat);
            log.info("[{}] Created new stat {}", this.schema, stat);
            return stat;
        } finally {
            lock.unlock();
        }
    }

    public void deleteStat(String statName) {
        statName = this.getStatName(statName);
        final Lock lock = this.getStatLock(statName);
        lock.lock();
        try {
            final Stat stat = this.stats.remove(statName);
            if (stat != null) {
                this.statRepository.removeStat(stat.getRepositoryId());
                log.info("[{}] Removed stat {}", this.schema, stat);
            }
        } finally {
            lock.unlock();
        }
    }
}
