package de.timmi6790.mpstats.api.versions.v1.common.player_stats.generator.generators;

import de.timmi6790.mpstats.api.versions.v1.common.player_stats.generator.BaseStatGenerator;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.generator.StatGeneratorData;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.models.GeneratedPlayerEntry;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.models.PlayerEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LevelGenerator extends BaseStatGenerator {
    protected int calculateLevel(long experiences) {
        int level = 0;
        long requiredExp = 0;
        while (experiences >= requiredExp) {
            if (level < 10) {
                requiredExp += 500;
            } else if (level < 20) {
                requiredExp += 1_000;
            } else {
                final long increaseFactor = level / 20;
                requiredExp += 1_000 + (increaseFactor * 1_000);
            }

            if (experiences >= requiredExp) {
                experiences -= requiredExp;
                level++;
            }
        }

        return level;
    }

    @Override
    public Collection<GeneratedPlayerEntry> generateStats(final StatGeneratorData generatorData) {
        final List<GeneratedPlayerEntry> levelEntries = new ArrayList<>();

        final List<PlayerEntry> expEntries = generatorData.getPlayerEntries(
                entry -> entry.getLeaderboard().getStat().getWebsiteName().equalsIgnoreCase(EXP_EARNED_WEBSITE_STAT_NAME)
                        && entry.isPresent()
        );
        for (final PlayerEntry entry : expEntries) {
            final int level = this.calculateLevel(entry.getScore());
            levelEntries.add(
                    new GeneratedPlayerEntry(
                            "Level",
                            level,
                            entry.getLeaderboard()
                    )
            );
        }

        return levelEntries;
    }
}
