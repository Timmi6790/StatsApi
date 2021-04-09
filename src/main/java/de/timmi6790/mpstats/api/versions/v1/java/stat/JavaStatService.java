package de.timmi6790.mpstats.api.versions.v1.java.stat;

import de.timmi6790.mpstats.api.versions.v1.java.stat.repository.JavaStatRepository;
import de.timmi6790.mpstats.api.versions.v1.java.stat.repository.models.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JavaStatService {
    private final JavaStatRepository javaStatRepository;

    private final Set<String> statNames = Collections.synchronizedSet(new TreeSet<>(String.CASE_INSENSITIVE_ORDER));

    @Autowired
    public JavaStatService(final JavaStatRepository javaStatRepository) {
        this.javaStatRepository = javaStatRepository;

        for (final Stat stat : this.getStats()) {
            this.statNames.add(stat.getStatName());
        }
    }

    public boolean hasStat(final String statName) {
        return this.statNames.contains(statName);
    }

    public List<Stat> getStats() {
        return this.javaStatRepository.getStats();
    }

    public Optional<Stat> getStat(final String statName) {
        if (this.hasStat(statName)) {
            return this.javaStatRepository.getStat(statName);
        }

        return Optional.empty();
    }

    public Stat getOrCreateStat(final String websiteName,
                                final String statName,
                                final String cleanName,
                                final boolean isAchievement) {
        if (this.hasStat(statName)) {
            return this.getStat(statName).orElseThrow(RuntimeException::new);
        }

        final Stat stat = this.javaStatRepository.createStat(websiteName, statName, cleanName, isAchievement);
        this.statNames.add(stat.getStatName());
        return stat;
    }

    public void deleteStat(final String statName) {
        final Optional<Stat> statOpt = this.getStat(statName);
        if (statOpt.isPresent()) {
            this.statNames.remove(statName);
            this.javaStatRepository.removeStat(statOpt.get().getRepositoryId());
        }
    }
}
