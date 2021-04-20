package de.timmi6790.mpstats.api.utilities;

import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;

import java.util.concurrent.atomic.AtomicInteger;

public class StatUtilities {
    private static final AtomicInteger STAT_ID = new AtomicInteger(0);

    public static String generateStatName() {
        return "Stat" + STAT_ID.incrementAndGet();
    }

    public static Stat generateStat(final StatService statService) {
        final String statName = generateStatName();
        return generateStat(statService, statName);
    }

    public static Stat generateStat(final StatService statService, final String statName) {
        final String websiteName = generateStatName();
        final String cleanName = generateStatName();
        final boolean achievement = true;

        return statService.getStatOrCreate(websiteName, statName, cleanName, achievement);
    }
}
