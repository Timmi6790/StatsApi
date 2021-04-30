package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_cache;

import com.google.common.collect.Lists;
import de.timmi6790.mpstats.api.AbstractSpringBootTest;
import de.timmi6790.mpstats.api.utilities.LeaderboardUtilities;
import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_cache.models.LeaderboardSaveCache;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractLeaderboardCacheServiceTest<P extends Player> extends AbstractSpringBootTest {
    protected abstract LeaderboardCacheService<P> getLeaderboardCacheService();

    protected abstract LeaderboardService getLeaderboardService();

    protected abstract GameService getGameService();

    protected abstract StatService getStatService();

    protected abstract BoardService getBoardService();

    protected abstract P generatePlayer();

    protected Leaderboard generateLeaderboard() {
        return LeaderboardUtilities.generateLeaderboard(
                this.getLeaderboardService(),
                this.getGameService(),
                this.getStatService(),
                this.getBoardService()
        );
    }

    protected List<LeaderboardEntry<P>> generateEntries(final int count) {
        final List<LeaderboardEntry<P>> entries = Lists.newArrayListWithCapacity(count);

        for (int index = 0; count > index; index++) {
            entries.add(
                    new LeaderboardEntry<>(
                            this.generatePlayer(),
                            ThreadLocalRandom.current().nextLong()
                    )
            );
        }

        return entries;
    }

    @Test
    void saveLeaderboardEntryPosition() {
        final Leaderboard leaderboard = this.generateLeaderboard();
        final LocalDateTime saveTime = LocalDateTime.now();
        final List<LeaderboardEntry<P>> entries = this.generateEntries(100);

        // Insert
        this.getLeaderboardCacheService().saveLeaderboardEntryPosition(leaderboard, entries, saveTime);

        final Optional<LeaderboardSaveCache<P>> cacheFound = this.getLeaderboardCacheService().retrieveLeaderboardEntryPosition(leaderboard);
        assertThat(cacheFound).isPresent();

        final LeaderboardSaveCache<P> cache = cacheFound.get();
        assertThat(cache.getEntries()).isEqualTo(entries);
        assertThat(cache.getSaveTime()).isEqualTo(saveTime);
    }
}