package de.timmi6790.mpstats.api.versions.v1.common.leaderboard;

import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.LeaderboardRepository;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractLeaderboardServiceTest {
    private static final AtomicInteger GAME_ID = new AtomicInteger(0);
    private static final AtomicInteger CATEGORY_ID = new AtomicInteger(0);
    private static final AtomicInteger STAT_ID = new AtomicInteger(0);
    private static final AtomicInteger BOARD_ID = new AtomicInteger(0);

    private final LeaderboardService leaderboardService;
    private final LeaderboardRepository leaderboardRepository;

    private final GameService gameService;
    private final BoardService boardService;
    private final StatService statService;

    public AbstractLeaderboardServiceTest(final Supplier<LeaderboardService> leaderboardServiceSupplier,
                                          final GameService gameService,
                                          final StatService statService,
                                          final BoardService boardService) {
        this.leaderboardService = leaderboardServiceSupplier.get();
        this.leaderboardRepository = this.leaderboardService.getLeaderboardRepository();

        this.gameService = gameService;
        this.boardService = boardService;
        this.statService = statService;
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

        return this.gameService.getOrCreateGame(websiteName, gameName, cleanName, categoryName);
    }

    private Board generateBoard() {
        final String boardName = this.generateBoardName();
        final String websiteName = this.generateBoardName();
        final String cleanName = this.generateBoardName();
        final int updateTime = 1;

        return this.boardService.getBoardOrCreate(boardName, websiteName, cleanName, updateTime);
    }

    private Stat generateStat() {
        final String statName = this.generateStatName();
        final String websiteName = this.generateStatName();
        final String cleanName = this.generateStatName();
        final boolean achievement = true;

        return this.statService.getStatOrCreate(websiteName, statName, cleanName, achievement);
    }

    private Leaderboard generateLeaderboard() {
        final Game game = this.generateGame();
        return this.generateLeaderboard(game);
    }

    private Leaderboard generateLeaderboard(final Game game) {
        final Stat stat = this.generateStat();
        final Board board = this.generateBoard();
        final boolean deprecated = true;

        return this.leaderboardService.getLeaderboardOrCreate(game, stat, board, deprecated);
    }

    @Test
    void getLeaderboards() {
        final List<Leaderboard> requiredLeaderboards = new ArrayList<>();
        for (int count = 0; 10 > count; count++) {
            requiredLeaderboards.add(this.generateLeaderboard());
        }

        final List<Leaderboard> foundLeaderboards = this.leaderboardService.getLeaderboards();
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

        final List<Leaderboard> foundLeaderboards = this.leaderboardService.getLeaderboards(requiredGame);
        assertThat(foundLeaderboards).containsOnly(requiredLeaderboards.toArray(new Leaderboard[0]));
    }

    @Test
    void getLeaderboard() {
        final Game game = this.generateGame();
        final Stat stat = this.generateStat();
        final Board board = this.generateBoard();
        final boolean deprecated = true;

        final Optional<Leaderboard> leaderboardNotFound = this.leaderboardService.getLeaderboard(game, stat, board);
        assertThat(leaderboardNotFound).isNotPresent();

        // Create leaderboard
        final Leaderboard leaderboard = this.leaderboardService.getLeaderboardOrCreate(game, stat, board, deprecated);

        final Optional<Leaderboard> leaderboardFound = this.leaderboardService.getLeaderboard(game, stat, board);
        assertThat(leaderboardFound)
                .isPresent()
                .contains(leaderboard);

        // Verify cache
        final Optional<Leaderboard> leaderboardFoundCache = this.leaderboardService.getLeaderboard(game, stat, board);
        assertThat(leaderboardFoundCache)
                .isPresent()
                .contains(leaderboard);

        // Verify none cache
        final Optional<Leaderboard> leaderboardFoundNoneCache = this.leaderboardRepository.getLeaderboard(game, stat, board);
        assertThat(leaderboardFoundNoneCache)
                .isPresent()
                .contains(leaderboard);
    }

    @Test
    void getLeaderboard_by_id() {
        final Game game = this.generateGame();
        final Stat stat = this.generateStat();
        final Board board = this.generateBoard();
        final boolean deprecated = true;

        // Create leaderboard
        final Leaderboard leaderboard = this.leaderboardService.getLeaderboardOrCreate(game, stat, board, deprecated);

        final Optional<Leaderboard> leaderboardFound = this.leaderboardService.getLeaderboard(leaderboard.getRepositoryId());
        assertThat(leaderboardFound)
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
        final Leaderboard leaderboard = this.leaderboardService.getLeaderboardOrCreate(game, stat, board, deprecated);

        // Verify content
        assertThat(leaderboard.getGame()).isEqualTo(game);
        assertThat(leaderboard.getStat()).isEqualTo(stat);
        assertThat(leaderboard.getBoard()).isEqualTo(board);
        assertThat(leaderboard.isDeprecated()).isEqualTo(deprecated);

        // Verify repository
        final Optional<Leaderboard> leaderboardRepositoryOpt = this.leaderboardRepository.getLeaderboard(game, stat, board);
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
        final Leaderboard leaderboard = this.leaderboardService.getLeaderboardOrCreate(game, stat, board, deprecated);

        // Try duplicate
        final Leaderboard leaderboardDuplicate = this.leaderboardService.getLeaderboardOrCreate(game, stat, board, deprecated);

        assertThat(leaderboard).isEqualTo(leaderboardDuplicate);
    }
}