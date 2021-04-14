package de.timmi6790.mpstats.api.versions.v1.website.parser;

import de.timmi6790.commons.builders.SetBuilder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class WebsiteFilter {
    private static final Set<String> GENERAL_FILTERED_STATS = SetBuilder.<String>ofHashSet()
            .addAll(
                    "KDR",
                    "Total games",
                    "Damage Taken in PvP",
                    "Damage Taken",
                    "Damage Dealt",
                    "Blue Kills",
                    "Yellow Kills",
                    "Red Kills",
                    "Green Kills",
                    "Blue Deaths",
                    "Yellow Deaths",
                    "Red Deaths",
                    "Green Deaths",
                    "SWAT Kills",
                    "Bombers Kills",
                    "SWAT Deaths",
                    "Bombers Deaths"
            ).build();

    public boolean isStatFiltered(final String game, final String stat) {
        if (GENERAL_FILTERED_STATS.contains(stat)) {
            return true;
        }

        return false;
    }
}
