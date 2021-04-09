package de.timmi6790.mpstats.api.versions.v1.java.stat;

import com.google.common.util.concurrent.Striped;
import de.timmi6790.mpstats.api.versions.v1.java.stat.repository.JavaStatRepository;
import de.timmi6790.mpstats.api.versions.v1.java.stat.repository.models.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

@Service
public class JavaStatService {
    private final JavaStatRepository javaStatRepository;

    private final Striped<Lock> statLock = Striped.lock(32);
    private final Map<String, Stat> stats;

    @Autowired
    public JavaStatService(final JavaStatRepository javaStatRepository) {
        this.javaStatRepository = javaStatRepository;

        // Load existing stats from repository
        final List<Stat> existingStats = javaStatRepository.getStats();
        this.stats = new LinkedCaseInsensitiveMap<>(existingStats.size());
        for (final Stat stat : existingStats) {
            this.stats.put(stat.getStatName(), stat);
        }
    }

    private Lock getStatLock(final String statName) {
        return this.statLock.get(statName.toLowerCase());
    }

    public boolean hasStat(final String statName) {
        return this.stats.containsKey(statName);
    }

    public List<Stat> getStats() {
        return new ArrayList<>(this.stats.values());
    }

    public Optional<Stat> getStat(final String statName) {
        return Optional.ofNullable(this.stats.get(statName));
    }

    public Stat getOrCreateStat(final String websiteName,
                                final String statName,
                                final String cleanName,
                                final boolean isAchievement) {
        final Lock lock = this.getStatLock(statName);
        lock.lock();
        try {
            if (this.hasStat(statName)) {
                return this.getStat(statName).orElseThrow(RuntimeException::new);
            }

            final Stat stat = this.javaStatRepository.createStat(websiteName, statName, cleanName, isAchievement);
            this.stats.put(stat.getStatName(), stat);
            return stat;
        } finally {
            lock.unlock();
        }
    }

    public void deleteStat(final String statName) {
        final Lock lock = this.getStatLock(statName);
        lock.lock();
        try {
            final Stat stat = this.stats.remove(statName);
            if (stat != null) {
                this.javaStatRepository.removeStat(stat.getRepositoryId());
            }
        } finally {
            lock.unlock();
        }
    }
}
