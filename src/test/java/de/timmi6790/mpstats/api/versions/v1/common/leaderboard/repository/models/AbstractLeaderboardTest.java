package de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models;

import de.timmi6790.mpstats.api.utilities.LeaderboardUtilities;
import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AllArgsConstructor
public abstract class AbstractLeaderboardTest {
    private final LeaderboardService leaderboardService;

    private final GameService gameService;
    private final BoardService boardService;
    private final StatService statService;

    protected Leaderboard generateLeaderboard() {
        return LeaderboardUtilities.generateLeaderboard(
                this.leaderboardService,
                this.gameService,
                this.statService,
                this.boardService
        );
    }

    protected Leaderboard getRepositoryLeaderboard(final Leaderboard leaderboard) {
        final Optional<Leaderboard> repositoryLeaderboardOpt = this.leaderboardService.getLeaderboard(leaderboard.getRepositoryId());
        assertThat(repositoryLeaderboardOpt).isPresent();
        return repositoryLeaderboardOpt.get();
    }

    @Test
    void setDeprecated() {
        final Leaderboard leaderboard = this.generateLeaderboard();
        final boolean newValue = !leaderboard.isDeprecated();

        leaderboard.setDeprecated(newValue);
        assertThat(leaderboard.isDeprecated()).isEqualTo(newValue);

        final Leaderboard repositoryLeaderboard = this.getRepositoryLeaderboard(leaderboard);
        assertThat(repositoryLeaderboard.isDeprecated()).isEqualTo(newValue);
    }

    @Test
    void setLastSaveTime() {
        final Leaderboard leaderboard = this.generateLeaderboard();
        final ZonedDateTime newValue = ZonedDateTime.now();

        leaderboard.setLastSaveTime(newValue);
        assertThat(leaderboard.getLastSaveTime()).isEqualTo(newValue);

        final Leaderboard repositoryLeaderboard = this.getRepositoryLeaderboard(leaderboard);
        assertThat(repositoryLeaderboard.getLastSaveTime()).isEqualToIgnoringNanos(newValue);
    }

    @Test
    void setLastCacheSaveTime() {
        final Leaderboard leaderboard = this.generateLeaderboard();
        final ZonedDateTime newValue = ZonedDateTime.now();

        leaderboard.setLastCacheSaveTime(newValue);
        assertThat(leaderboard.getLastCacheSaveTime()).isEqualTo(newValue);

        final Leaderboard repositoryLeaderboard = this.getRepositoryLeaderboard(leaderboard);
        assertThat(repositoryLeaderboard.getLastCacheSaveTime()).isEqualToIgnoringNanos(newValue);
    }

    @Test
    void setDeprecated_empty_constructor() {
        final Leaderboard leaderboard = new Leaderboard();
        assertThrows(UnsupportedOperationException.class, () -> leaderboard.setDeprecated(true));
    }

    @Test
    void setLastSaveTime_empty_constructor() {
        final Leaderboard leaderboard = new Leaderboard();
        final ZonedDateTime lastSaveTime = ZonedDateTime.now();
        assertThrows(UnsupportedOperationException.class, () -> leaderboard.setLastSaveTime(lastSaveTime));
    }

    @Test
    void setLastCacheSaveTime_empty_constructor() {
        final Leaderboard leaderboard = new Leaderboard();
        final ZonedDateTime lastSaveTime = ZonedDateTime.now();
        assertThrows(UnsupportedOperationException.class, () -> leaderboard.setLastCacheSaveTime(lastSaveTime));
    }
}