package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save;

import de.timmi6790.mpstats.api.utilities.LeaderboardEntryUtilities;
import de.timmi6790.mpstats.api.utilities.LeaderboardUtilities;
import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardSave;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import lombok.AccessLevel;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@Getter(AccessLevel.PROTECTED)
public abstract class AbstractLeaderboardSaveServiceTest<P extends Player> {
    protected final GameService gameService;
    protected final StatService statService;
    protected final BoardService boardService;
    private final LeaderboardSaveService<P> saveService;
    private final LeaderboardService leaderboardService;

    protected AbstractLeaderboardSaveServiceTest(final LeaderboardSaveService<P> saveService,
                                                 final LeaderboardService leaderboardService,
                                                 final GameService gameService,
                                                 final StatService statService,
                                                 final BoardService boardService) {
        this.saveService = saveService;
        this.leaderboardService = leaderboardService;
        this.gameService = gameService;
        this.statService = statService;
        this.boardService = boardService;
    }

    protected PlayerService<P> getPlayerService() {
        return this.saveService.getPlayerService();
    }

    protected List<LeaderboardEntry<P>> generateEntries(final int count) {
        return LeaderboardEntryUtilities.generateEntries(this.getPlayerService(), count);
    }

    protected Leaderboard generateLeaderboard() {
        return LeaderboardUtilities.generateLeaderboard(
                this.getLeaderboardService(),
                this.gameService,
                this.statService,
                this.boardService
        );
    }

    protected void verifyLeaderboardEntries(final List<LeaderboardEntry<P>> insertedEntries,
                                            final List<LeaderboardEntry<P>> repositoryEntries) {
        assertThat(insertedEntries).hasSameSizeAs(repositoryEntries);
        for (int index = 0; insertedEntries.size() > index; index++) {
            final LeaderboardEntry<P> insertedEntry = insertedEntries.get(index);
            final LeaderboardEntry<P> repositoryEntry = repositoryEntries.get(index);

            assertThat(insertedEntry.getScore()).isEqualTo(repositoryEntry.getScore());
            this.verifyPlayer(insertedEntry.getPlayer(), repositoryEntry.getPlayer());
        }
    }

    protected long getDateTimeDifferenceInSeconds(final ZonedDateTime firstDateTime, final ZonedDateTime secondDateTime) {
        final long unixSecondsFirst = firstDateTime.toEpochSecond();
        final long unixSecondsSecond = secondDateTime.toEpochSecond();
        return Math.abs(unixSecondsFirst - unixSecondsSecond);
    }

    protected Optional<List<LeaderboardEntry<P>>> getSavedLeaderboardEntries(final Leaderboard leaderboard,
                                                                             final ZonedDateTime saveTime) {
        return this.saveService.retrieveLeaderboardSave(leaderboard, saveTime)
                .map(LeaderboardSave::getEntries);
    }

    protected abstract void verifyPlayer(P insertedPlayer, P player);

    @Test
    void getLeaderboardSaveTimes() {
        final Leaderboard leaderboard = this.generateLeaderboard();

        final List<ZonedDateTime> nothingFound = this.saveService.getLeaderboardSaveTimes(leaderboard);
        assertThat(nothingFound).isEmpty();

        final int addedEntries = 3;
        for (int count = 0; addedEntries > count; count++) {
            this.saveService.saveLeaderboardEntries(leaderboard, this.generateEntries(1), ZonedDateTime.now());
        }

        final List<ZonedDateTime> threeEntriesFound = this.saveService.getLeaderboardSaveTimes(leaderboard);
        assertThat(threeEntriesFound).hasSize(addedEntries);
    }

    @Test
    void saveLeaderboardEntries() {
        final Leaderboard leaderboard = this.generateLeaderboard();

        // Save entries
        final int generateEntries = 100;
        final List<LeaderboardEntry<P>> entries = this.generateEntries(generateEntries);
        final ZonedDateTime saveTime = ZonedDateTime.now();
        this.saveService.saveLeaderboardEntries(leaderboard, entries, saveTime);

        final Optional<LeaderboardSave<P>> foundSave = this.saveService.retrieveLeaderboardSave(leaderboard, saveTime);
        assertThat(foundSave)
                .isPresent();
        assertThat(foundSave.get().getSaveTime()).isEqualToIgnoringNanos(saveTime);
        this.verifyLeaderboardEntries(entries, foundSave.get().getEntries());
    }

    @Test
    void saveLeaderboardEntries_empty() {
        final Leaderboard leaderboard = this.generateLeaderboard();
        final ZonedDateTime saveTime = ZonedDateTime.now();

        // Try to save an empty list
        this.saveService.saveLeaderboardEntries(leaderboard, new ArrayList<>(), saveTime);

        final Optional<LeaderboardSave<P>> notFound = this.saveService.retrieveLeaderboardSave(leaderboard, saveTime);
        assertThat(notFound)
                .isNotPresent();

        final List<ZonedDateTime> noEntriesFound = this.saveService.getLeaderboardSaveTimes(leaderboard);
        assertThat(noEntriesFound)
                .isEmpty();
    }

    @Test
    void retrieveLeaderboardSave_time_checks() {
        final Leaderboard leaderboard = this.generateLeaderboard();

        // First entries
        final ZonedDateTime firstSaveTime = ZonedDateTime.now();
        final List<LeaderboardEntry<P>> firstEntries = this.generateEntries(1);

        // Second entries
        final ZonedDateTime secondSaveTime = firstSaveTime.plus(1, ChronoUnit.HOURS);
        final List<LeaderboardEntry<P>> secondEntries = this.generateEntries(1);

        // Third entries
        final ZonedDateTime thirdSaveTime = secondSaveTime.plus(1, ChronoUnit.HOURS);
        final List<LeaderboardEntry<P>> thirdEntries = this.generateEntries(1);

        // Insert all entries
        this.saveService.saveLeaderboardEntries(leaderboard, firstEntries, firstSaveTime);
        this.saveService.saveLeaderboardEntries(leaderboard, secondEntries, secondSaveTime);
        this.saveService.saveLeaderboardEntries(leaderboard, thirdEntries, thirdSaveTime);

        // Verify with the exact same time
        final Optional<List<LeaderboardEntry<P>>> foundFirst = this.getSavedLeaderboardEntries(leaderboard, firstSaveTime);
        assertThat(foundFirst).isPresent();
        this.verifyLeaderboardEntries(firstEntries, foundFirst.get());

        final Optional<List<LeaderboardEntry<P>>> foundSecond = this.getSavedLeaderboardEntries(leaderboard, secondSaveTime);
        assertThat(foundSecond).isPresent();
        this.verifyLeaderboardEntries(secondEntries, foundSecond.get());

        final Optional<List<LeaderboardEntry<P>>> foundThird = this.getSavedLeaderboardEntries(leaderboard, thirdSaveTime);
        assertThat(foundThird).isPresent();
        this.verifyLeaderboardEntries(thirdEntries, foundThird.get());

        // Verify above and below
        final Optional<List<LeaderboardEntry<P>>> foundBelowFirst = this.getSavedLeaderboardEntries(leaderboard, firstSaveTime.minusDays(1));
        assertThat(foundBelowFirst).isPresent();
        this.verifyLeaderboardEntries(firstEntries, foundBelowFirst.get());

        final Optional<List<LeaderboardEntry<P>>> foundAboveThird = this.getSavedLeaderboardEntries(leaderboard, thirdSaveTime.plusDays(1));
        assertThat(foundAboveThird).isPresent();
        this.verifyLeaderboardEntries(thirdEntries, foundAboveThird.get());

        // Verify between closets
        final double lowerDifference = this.getDateTimeDifferenceInSeconds(firstSaveTime, secondSaveTime) / 2D;
        final Optional<List<LeaderboardEntry<P>>> foundLowerSecond = this.getSavedLeaderboardEntries(
                leaderboard,
                secondSaveTime.minus((long) (lowerDifference - 1), ChronoUnit.SECONDS)
        );
        assertThat(foundLowerSecond).isPresent();
        this.verifyLeaderboardEntries(secondEntries, foundLowerSecond.get());

        final double upperDifference = this.getDateTimeDifferenceInSeconds(thirdSaveTime, secondSaveTime) / 2D;
        final Optional<List<LeaderboardEntry<P>>> foundUpperSecond = this.getSavedLeaderboardEntries(
                leaderboard,
                secondSaveTime.plus((long) (upperDifference - 1), ChronoUnit.SECONDS)
        );
        assertThat(foundUpperSecond).isPresent();
        this.verifyLeaderboardEntries(secondEntries, foundUpperSecond.get());
    }

    @Test
    void retrieveLeaderboardSaves() {
        // First entries
        final Leaderboard leaderboard = this.generateLeaderboard();
        final ZonedDateTime firstSaveTime = ZonedDateTime.now();
        final List<LeaderboardEntry<P>> firstEntries = this.generateEntries(1);
        this.saveService.saveLeaderboardEntries(leaderboard, firstEntries, firstSaveTime);

        // Second entries
        final Leaderboard leaderboard2 = this.generateLeaderboard();
        final ZonedDateTime secondSaveTime = firstSaveTime.plus(1, ChronoUnit.HOURS);
        final List<LeaderboardEntry<P>> secondEntries = this.generateEntries(1);
        this.saveService.saveLeaderboardEntries(leaderboard2, secondEntries, secondSaveTime);

        final Map<Leaderboard, LeaderboardSave<P>> results = this.saveService.retrieveLeaderboardSaves(
                Arrays.asList(leaderboard, leaderboard2),
                ZonedDateTime.now()
        );
        assertThat(results).hasSize(2);

        // First found save
        final LeaderboardSave<P> save = results.get(leaderboard);
        assertThat(save.getSaveTime()).isEqualToIgnoringNanos(firstSaveTime);
        this.verifyLeaderboardEntries(firstEntries, save.getEntries());

        // Second found save
        final LeaderboardSave<P> save2 = results.get(leaderboard2);
        assertThat(save2.getSaveTime()).isEqualToIgnoringNanos(secondSaveTime);
        this.verifyLeaderboardEntries(secondEntries, save2.getEntries());
    }

    @Test
    void retrieveLeaderboardSaves_no_results() {
        final Leaderboard leaderboard = this.generateLeaderboard();
        final Map<Leaderboard, LeaderboardSave<P>> emptyMap = this.saveService.retrieveLeaderboardSaves(
                Collections.singletonList(leaderboard),
                ZonedDateTime.now()
        );
        assertThat(emptyMap).isEmpty();
    }

    @Test
    void retrieveLeaderboardSaves_empty_list() {
        this.generateLeaderboard();

        final Map<Leaderboard, LeaderboardSave<P>> emptyMap = this.saveService.retrieveLeaderboardSaves(Collections.emptyList(), ZonedDateTime.now());
        assertThat(emptyMap).isEmpty();
    }
}