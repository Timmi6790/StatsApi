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
        final PositionCalculation positionCalculation = new PositionCalculation(startPosition);
        final List<LeaderboardPositionEntry<P>> convertedList = Lists.newArrayListWithCapacity(entries.size());
        for (final LeaderboardEntry<P> entry : entries) {
            convertedList.add(
                    new LeaderboardPositionEntry<>(
                            entry,
                            positionCalculation.addScore(entry.getScore())
                    )
            );
        }

        return convertedList;
    }
}
