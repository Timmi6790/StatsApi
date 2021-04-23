package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_saves;

import de.timmi6790.mpstats.api.utilities.LeaderboardEntryUtilities;
import de.timmi6790.mpstats.api.utilities.LeaderboardUtilities;
import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_saves.repository.postgres.LeaderboardSavePostgresRepository;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardSave;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.RepositoryPlayer;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import lombok.AccessLevel;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Getter(value = AccessLevel.PROTECTED)
public abstract class AbstractLeaderboardSaveServiceTest<P extends Player, R extends Player & RepositoryPlayer> {
    private final LeaderboardSaveService<P, R> saveService;
    private final LeaderboardService leaderboardService;
    protected final GameService gameService;
    protected final StatService statService;
    protected final BoardService boardService;

    protected AbstractLeaderboardSaveServiceTest(final LeaderboardSaveService<P, R> saveService,
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

    protected PlayerService<R> getPlayerService() {
        return this.saveService.getPlayerService();
    }

    protected LeaderboardSavePostgresRepository<R> getRepository() {
        return this.saveService.getRepository();
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
                                            final List<LeaderboardEntry<R>> repositoryEntries) {
        assertThat(insertedEntries).hasSameSizeAs(repositoryEntries);
        for (int index = 0; insertedEntries.size() > index; index++) {
            final LeaderboardEntry<P> insertedEntry = insertedEntries.get(index);
            final LeaderboardEntry<R> repositoryEntry = repositoryEntries.get(index);

            assertThat(insertedEntry.getScore()).isEqualTo(repositoryEntry.getScore());
            this.verifyPlayer(insertedEntry.getPlayer(), repositoryEntry.getPlayer());
        }
    }

    protected long getDateTimeDifferenceInSeconds(final LocalDateTime firstDateTime, final LocalDateTime secondDateTime) {
        final long unixSecondsFirst = firstDateTime.toEpochSecond(ZoneOffset.ofHours(0));
        final long unixSecondsSecond = secondDateTime.toEpochSecond(ZoneOffset.ofHours(0));
        return Math.abs(unixSecondsFirst - unixSecondsSecond);
    }

    protected Optional<List<LeaderboardEntry<R>>> getSavedLeaderboardEntries(final Leaderboard leaderboard,
                                                                             final LocalDateTime saveTime) {
        return this.saveService.getLeaderboardSave(leaderboard, saveTime)
                .map(LeaderboardSave::getEntries);
    }

    protected abstract void verifyPlayer(P insertedPlayer, R repositoryPlayer);

    @Test
    void getLeaderboardSaveTimes() {
        final Leaderboard leaderboard = this.generateLeaderboard();

        final List<LocalDateTime> nothingFound = this.saveService.getLeaderboardSaveTimes(leaderboard);
        assertThat(nothingFound).isEmpty();

        final int addedEntries = 3;
        for (int count = 0; addedEntries > count; count++) {
            this.saveService.saveLeaderboardEntries(leaderboard, this.generateEntries(1), LocalDateTime.now());
        }

        final List<LocalDateTime> threeEntriesFound = this.saveService.getLeaderboardSaveTimes(leaderboard);
        assertThat(threeEntriesFound).hasSize(addedEntries);
    }

    @Test
    void saveLeaderboardEntries() {
        final Leaderboard leaderboard = this.generateLeaderboard();

        // Save entries
        final int generateEntries = 100;
        final List<LeaderboardEntry<P>> entries = this.generateEntries(generateEntries);
        final LocalDateTime saveTime = LocalDateTime.now();
        this.saveService.saveLeaderboardEntries(leaderboard, entries, saveTime);

        final Optional<LeaderboardSave<R>> foundSave = this.saveService.getLeaderboardSave(leaderboard, saveTime);
        assertThat(foundSave)
                .isPresent();
        assertThat(foundSave.get().getSaveTime()).isEqualToIgnoringNanos(saveTime);
        this.verifyLeaderboardEntries(entries, foundSave.get().getEntries());
    }

    @Test
    void saveLeaderboardEntries_empty() {
        final Leaderboard leaderboard = this.generateLeaderboard();
        final LocalDateTime saveTime = LocalDateTime.now();

        // Try to save an empty list
        this.saveService.saveLeaderboardEntries(leaderboard, new ArrayList<>(), saveTime);

        final Optional<LeaderboardSave<R>> notFound = this.saveService.getLeaderboardSave(leaderboard, saveTime);
        assertThat(notFound)
                .isNotPresent();

        final List<LocalDateTime> noEntriesFound = this.saveService.getLeaderboardSaveTimes(leaderboard);
        assertThat(noEntriesFound)
                .isEmpty();
    }

    @Test
    void getLeaderboardEntries_time_checks() {
        final Leaderboard leaderboard = this.generateLeaderboard();

        // First entries
        final LocalDateTime firstSaveTime = LocalDateTime.now();
        final List<LeaderboardEntry<P>> firstEntries = this.generateEntries(1);

        // Second entries
        final LocalDateTime secondSaveTime = firstSaveTime.plus(1, ChronoUnit.HOURS);
        final List<LeaderboardEntry<P>> secondEntries = this.generateEntries(1);

        // Third entries
        final LocalDateTime thirdSaveTime = secondSaveTime.plus(1, ChronoUnit.HOURS);
        final List<LeaderboardEntry<P>> thirdEntries = this.generateEntries(1);

        // Insert all entries
        this.saveService.saveLeaderboardEntries(leaderboard, firstEntries, firstSaveTime);
        this.saveService.saveLeaderboardEntries(leaderboard, secondEntries, secondSaveTime);
        this.saveService.saveLeaderboardEntries(leaderboard, thirdEntries, thirdSaveTime);

        // Verify with the exact same time
        final Optional<List<LeaderboardEntry<R>>> foundFirst = this.getSavedLeaderboardEntries(leaderboard, firstSaveTime);
        assertThat(foundFirst).isPresent();
        this.verifyLeaderboardEntries(firstEntries, foundFirst.get());

        final Optional<List<LeaderboardEntry<R>>> foundSecond = this.getSavedLeaderboardEntries(leaderboard, secondSaveTime);
        assertThat(foundSecond).isPresent();
        this.verifyLeaderboardEntries(secondEntries, foundSecond.get());

        final Optional<List<LeaderboardEntry<R>>> foundThird = this.getSavedLeaderboardEntries(leaderboard, thirdSaveTime);
        assertThat(foundThird).isPresent();
        this.verifyLeaderboardEntries(thirdEntries, foundThird.get());

        // Verify above and below
        final Optional<List<LeaderboardEntry<R>>> foundBelowFirst = this.getSavedLeaderboardEntries(leaderboard, firstSaveTime.minusDays(1));
        assertThat(foundBelowFirst).isPresent();
        this.verifyLeaderboardEntries(firstEntries, foundBelowFirst.get());

        final Optional<List<LeaderboardEntry<R>>> foundAboveThird = this.getSavedLeaderboardEntries(leaderboard, thirdSaveTime.plusDays(1));
        assertThat(foundAboveThird).isPresent();
        this.verifyLeaderboardEntries(thirdEntries, foundAboveThird.get());

        // Verify between closets
        final double lowerDifference = this.getDateTimeDifferenceInSeconds(firstSaveTime, secondSaveTime) / 2D;
        final Optional<List<LeaderboardEntry<R>>> foundLowerSecond = this.getSavedLeaderboardEntries(
                leaderboard,
                secondSaveTime.minus((long) (lowerDifference - 1), ChronoUnit.SECONDS)
        );
        assertThat(foundLowerSecond).isPresent();
        this.verifyLeaderboardEntries(secondEntries, foundLowerSecond.get());

        final double upperDifference = this.getDateTimeDifferenceInSeconds(thirdSaveTime, secondSaveTime) / 2D;
        final Optional<List<LeaderboardEntry<R>>> foundUpperSecond = this.getSavedLeaderboardEntries(
                leaderboard,
                secondSaveTime.plus((long) (upperDifference - 1), ChronoUnit.SECONDS)
        );
        assertThat(foundUpperSecond).isPresent();
        this.verifyLeaderboardEntries(secondEntries, foundUpperSecond.get());
    }
}