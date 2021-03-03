package de.timmi6790.mpstats.api.versions.v1.java.leaderboard;

import de.timmi6790.mpstats.api.versions.v1.java.cache.JavaCache;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.models.JavaLeaderboardModel;
import de.timmi6790.mpstats.api.versions.v1.java.player.models.JavaPlayerModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class JavaLeaderboardService {
    private final JavaCache cache;

    public JavaLeaderboardService(final JavaCache javaCache) {
        this.cache = javaCache;
    }

    public List<JavaLeaderboardModel> getLeaderboard(final String game,
                                                     final String stat,
                                                     final String board,
                                                     final int startPosition,
                                                     final int endPosition,
                                                     final boolean filter,
                                                     final LocalDateTime dateTime) {
        final List<JavaLeaderboardModel> leaderboardModels = new ArrayList<>();
        for (int count = 0; 1_000 > count; count++) {
            leaderboardModels.add(
                    new JavaLeaderboardModel(
                            new JavaPlayerModel(
                                    "A" + ThreadLocalRandom.current().nextInt(5_000),
                                    UUID.randomUUID()
                            ),
                            count,
                            ThreadLocalRandom.current().nextInt(5_000) + count
                    )
            );
        }
        this.cache.storeLeaderboard(1, filter, leaderboardModels);
        return this.cache.retrieveLeaderboard(1, filter);
    }
}