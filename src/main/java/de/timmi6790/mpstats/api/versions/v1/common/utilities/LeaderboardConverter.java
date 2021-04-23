package de.timmi6790.mpstats.api.versions.v1.common.utilities;

import com.google.common.collect.Lists;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardPositionEntry;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.List;

@UtilityClass
public class LeaderboardConverter {
    public <P extends Player> List<LeaderboardPositionEntry<P>> convertEntries(final Collection<LeaderboardEntry<P>> entries) {
        return convertEntries(entries, 1);
    }

    public <P extends Player> List<LeaderboardPositionEntry<P>> convertEntries(final Collection<LeaderboardEntry<P>> entries,
                                                                               final int startPosition) {
        final List<LeaderboardPositionEntry<P>> convertedList = Lists.newArrayListWithCapacity(entries.size());

        int globalPosition = startPosition - 1;
        int position = globalPosition;
        long lastScore = Long.MIN_VALUE;
        for (final LeaderboardEntry<P> entry : entries) {
            globalPosition++;
            if (entry.getScore() > lastScore) {
                position = globalPosition;
            }

            convertedList.add(
                    new LeaderboardPositionEntry<>(
                            entry,
                            position
                    )
            );

            lastScore = entry.getScore();
        }

        return convertedList;
    }
}
