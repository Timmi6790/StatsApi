package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_cache;

import com.google.common.collect.Lists;
import de.timmi6790.mpstats.api.AbstractSpringBootTest;
import de.timmi6790.mpstats.api.utilities.LeaderboardUtilities;
import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardSave;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.*;
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

    protected void verifySave(final LeaderboardSave<P> save, final List<LeaderboardEntry<P>> entries, final ZonedDateTime saveTime) {
        assertThat(save.getEntries()).isEqualTo(entries);
        assertThat(save.getSaveTime()).isEqualTo(saveTime);
    }

    @Test
    void saveLeaderboardEntryPosition() {
        final Leaderboard leaderboard = this.generateLeaderboard();
        final ZonedDateTime saveTime = ZonedDateTime.now();
        final List<LeaderboardEntry<P>> entries = this.generateEntries(100);

        // Insert
        this.getLeaderboardCacheService().saveLeaderboardEntryPosition(leaderboard, entries, saveTime);

        final Optional<LeaderboardSave<P>> cacheFound = this.getLeaderboardCacheService().retrieveLeaderboardSave(leaderboard);
        assertThat(cacheFound).isPresent();

        final LeaderboardSave<P> cache = cacheFound.get();
        this.verifySave(cache, entries, saveTime);
    }

    @Test
    void retrieveLeaderboardSaves_missing_entry() {
        // Save only one and three into redis
        final Leaderboard leaderboard = this.generateLeaderboard();
        final ZonedDateTime lbSaveTime = ZonedDateTime.now();
        final List<LeaderboardEntry<P>> lbEntries = this.generateEntries(100);

        final Leaderboard leaderboard2 = this.generateLeaderboard();

        final Leaderboard leaderboard3 = this.generateLeaderboard();
        final ZonedDateTime lb3SaveTime = ZonedDateTime.now();
        final List<LeaderboardEntry<P>> lb3Entries = this.generateEntries(100);

        this.getLeaderboardCacheService().saveLeaderboardEntryPosition(leaderboard, lbEntries, lbSaveTime);
        this.getLeaderboardCacheService().saveLeaderboardEntryPosition(leaderboard3, lb3Entries, lb3SaveTime);

        final Map<Leaderboard, LeaderboardSave<P>> results = this.getLeaderboardCacheService().retrieveLeaderboardSaves(Arrays.asList(leaderboard, leaderboard2, leaderboard3));
        assertThat(results).hasSize(2);

        final LeaderboardSave<P> lbSave = results.get(leaderboard);
        this.verifySave(lbSave, lbEntries, lbSaveTime);

        final LeaderboardSave<P> lb3Save = results.get(leaderboard3);
        this.verifySave(lb3Save, lb3Entries, lb3SaveTime);
    }

    @Test
    void retrieveLeaderboardSaves_null_entry() {
        final Leaderboard leaderboard = this.generateLeaderboard();
        final Leaderboard leaderboard2 = this.generateLeaderboard();

        final Map<Leaderboard, LeaderboardSave<P>> emptyMap = this.getLeaderboardCacheService().retrieveLeaderboardSaves(Arrays.asList(leaderboard, leaderboard2));
        assertThat(emptyMap).isEmpty();
    }

    @Test
    void retrieveLeaderboardSaves_empty_list() {
        final Map<Leaderboard, LeaderboardSave<P>> emptyMap = this.getLeaderboardCacheService().retrieveLeaderboardSaves(Collections.emptyList());
        assertThat(emptyMap).isEmpty();
    }
}