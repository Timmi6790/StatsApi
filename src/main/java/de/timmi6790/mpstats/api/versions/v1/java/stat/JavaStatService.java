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
    }

    public boolean hasStat(final String statName) {
        return this.statNames.contains(statName);
    }

    public List<Stat> getStats() {
        return new ArrayList<>();
    }

    public Optional<Stat> getStat(final String statName) {
        return Optional.empty();
    }

    public Stat createStat(final String statName, final String websiteName, final boolean isAchievement) {
        return null;
    }

    public void deleteStat(final String gameName) {

    }
}
