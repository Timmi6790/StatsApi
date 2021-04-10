package de.timmi6790.mpstats.api.versions.v1.java.leaderboard;

import de.timmi6790.mpstats.api.AbstractIntegrationTest;
import de.timmi6790.mpstats.api.versions.v1.java.board.JavaBoardService;
import de.timmi6790.mpstats.api.versions.v1.java.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.java.board.repository.postgres.JavaBoardPostgresRepository;
import de.timmi6790.mpstats.api.versions.v1.java.game.JavaGameService;
import de.timmi6790.mpstats.api.versions.v1.java.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.java.game.repository.postgres.JavaGamePostgresRepository;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.repository.JavaLeaderboardRepository;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.repository.postgres.JavaLeaderboardPostgresRepository;
import de.timmi6790.mpstats.api.versions.v1.java.stat.JavaStatService;
import de.timmi6790.mpstats.api.versions.v1.java.stat.repository.models.Stat;
import de.timmi6790.mpstats.api.versions.v1.java.stat.repository.postgres.JavaStatPostgresRepository;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class JavaLeaderboardServiceTest {
    private static JavaLeaderboardService javaLeaderboardService;
    private static JavaLeaderboardRepository javaLeaderboardRepository;

    private static JavaGameService javaGameService;
    private static JavaBoardService javaBoardService;
    private static JavaStatService javaStatService;

    private static final AtomicInteger GAME_ID = new AtomicInteger(0);
    private static final AtomicInteger CATEGORY_ID = new AtomicInteger(0);
    private static final AtomicInteger STAT_ID = new AtomicInteger(0);
    private static final AtomicInteger BOARD_ID = new AtomicInteger(0);

    @BeforeAll
    static void beforeAll() {
        final Jdbi jdbi = AbstractIntegrationTest.jdbi();

        javaGameService = new JavaGameService(new JavaGamePostgresRepository(jdbi));
        javaBoardService = new JavaBoardService(new JavaBoardPostgresRepository(jdbi));
        javaStatService = new JavaStatService(new JavaStatPostgresRepository(jdbi));

        javaLeaderboardRepository = new JavaLeaderboardPostgresRepository(
                jdbi,
                javaGameService,
                javaStatService,
                javaBoardService
        );
        javaLeaderboardService = new JavaLeaderboardService(javaLeaderboardRepository);
    }

    private String generateGameName() {
        return "GAMELEADERBOARD" + GAME_ID.incrementAndGet();
    }

    private String generateCategoryName() {
        return "CATEGORYLEADERBOARDD" + CATEGORY_ID.incrementAndGet();
    }

    private String generateStatName() {
        return "STATLEADERBOARD" + STAT_ID.incrementAndGet();
    }

    private String generateBoardName() {
        return "BOARDLEADERBOARD" + BOARD_ID.incrementAndGet();
    }

    private Game generateGame() {
        final String gameName = this.generateGameName();
        final String websiteName = this.generateGameName();
        final String cleanName = this.generateGameName();
        final String categoryName = this.generateCategoryName();

        return javaGameService.getOrCreateGame(websiteName, gameName, cleanName, categoryName);
    }

    private Board generateBoard() {
        final String boardName = this.generateBoardName();
        final String websiteName = this.generateBoardName();
        final String cleanName = this.generateBoardName();
        final int updateTime = 1;

        return javaBoardService.getBordOrCreate(boardName, websiteName, cleanName, updateTime);
    }

    private Stat generateStat() {
        final String statName = this.generateStatName();
        final String websiteName = this.generateStatName();
        final String cleanName = this.generateStatName();
        final boolean achievement = true;

        return javaStatService.getStatOrCreate(websiteName, statName, cleanName, achievement);
    }

    private Leaderboard generateLeaderboard() {
        final Game game = this.generateGame();
        return this.generateLeaderboard(game);
    }

    private Leaderboard generateLeaderboard(final Game game) {
        final Stat stat = this.generateStat();
        final Board board = this.generateBoard();
        final boolean deprecated = true;

        return javaLeaderboardService.getLeaderboardOrCreate(game, stat, board, deprecated);
    }

    @Test
    void getLeaderboards() {
        final List<Leaderboard> requiredLeaderboards = new ArrayList<>();
        for (int count = 0; 10 > count; count++) {
            requiredLeaderboards.add(this.generateLeaderboard());
        }

        final List<Leaderboard> foundLeaderboards = javaLeaderboardService.getLeaderboards();
        assertThat(foundLeaderboards).containsAll(requiredLeaderboards);
    }

    @Test
    void getLeaderboards_game() {
        final Game requiredGame = this.generateGame();
        final List<Leaderboard> requiredLeaderboards = new ArrayList<>();
        for (int count = 0; 10 > count; count++) {
            requiredLeaderboards.add(this.generateLeaderboard(requiredGame));
        }

        // Generate more leaderboards that should not show up in the results
        for (int count = 0; 10 > count; count++) {
            this.generateLeaderboard();
        }

        final List<Leaderboard> foundLeaderboards = javaLeaderboardService.getLeaderboards(requiredGame);
        assertThat(foundLeaderboards).containsOnly(requiredLeaderboards.toArray(new Leaderboard[0]));
    }

    @Test
    void getLeaderboard() {
        final Game game = this.generateGame();
        final Stat stat = this.generateStat();
        final Board board = this.generateBoard();
        final boolean deprecated = true;

        final Optional<Leaderboard> leaderboardNotFound = javaLeaderboardService.getLeaderboard(game, stat, board);
        assertThat(leaderboardNotFound).isNotPresent();

        // Create leaderboard
        final Leaderboard leaderboard = javaLeaderboardService.getLeaderboardOrCreate(game, stat, board, deprecated);

        final Optional<Leaderboard> leaderboardFound = javaLeaderboardService.getLeaderboard(game, stat, board);
        assertThat(leaderboardFound)
                .isPresent()
                .contains(leaderboard);

        // Verify cache
        final Optional<Leaderboard> leaderboardFoundCache = javaLeaderboardService.getLeaderboard(game, stat, board);
        assertThat(leaderboardFoundCache)
                .isPresent()
                .contains(leaderboard);

        // Verify none cache
        final Optional<Leaderboard> leaderboardFoundNoneCache = javaLeaderboardRepository.getLeaderboard(game, stat, board);
        assertThat(leaderboardFoundNoneCache)
                .isPresent()
                .contains(leaderboard);
    }

    @Test
    void getLeaderboardOrCreate() {
        final Game game = this.generateGame();
        final Stat stat = this.generateStat();
        final Board board = this.generateBoard();
        final boolean deprecated = true;

        // Create leaderboard
        final Leaderboard leaderboard = javaLeaderboardService.getLeaderboardOrCreate(game, stat, board, deprecated);

        // Verify content
        assertThat(leaderboard.getGame()).isEqualTo(game);
        assertThat(leaderboard.getStat()).isEqualTo(stat);
        assertThat(leaderboard.getBoard()).isEqualTo(board);
        assertThat(leaderboard.isDeprecated()).isEqualTo(deprecated);

        // Verify repository
        final Optional<Leaderboard> leaderboardRepositoryOpt = javaLeaderboardRepository.getLeaderboard(game, stat, board);
        assertThat(leaderboardRepositoryOpt)
                .isPresent()
                .contains(leaderboard);
    }

    @Test
    void getLeaderboardOrCreate_duplicate() {
        final Game game = this.generateGame();
        final Stat stat = this.generateStat();
        final Board board = this.generateBoard();
        final boolean deprecated = true;

        // Create leaderboard
        final Leaderboard leaderboard = javaLeaderboardService.getLeaderboardOrCreate(game, stat, board, deprecated);

        // Try duplicate
        final Leaderboard leaderboardDuplicate = javaLeaderboardService.getLeaderboardOrCreate(game, stat, board, deprecated);

        assertThat(leaderboard).isEqualTo(leaderboardDuplicate);
    }
}