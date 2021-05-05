package de.timmi6790.mpstats.api.utilities;

import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LeaderboardEntryUtilities {
    public static <P extends Player> List<LeaderboardEntry<P>> generateEntries(final PlayerService<P> playerService,
                                                                               final int count) {
        // We use a stream here to leverage the parallel speed. Creating n players can take some time
        return IntStream.range(0, count)
                .parallel()
                .mapToObj(i -> (P) PlayerUtilities.generatePlayer(playerService))
                .map(player ->
                        new LeaderboardEntry<>(
                                player,
                                ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE)
                        )
                ).sorted(Comparator.comparing(LeaderboardEntry::getScore, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
}
