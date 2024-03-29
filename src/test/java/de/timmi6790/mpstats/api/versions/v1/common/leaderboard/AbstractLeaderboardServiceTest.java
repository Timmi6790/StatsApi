package de.timmi6790.mpstats.api.versions.v1.common.leaderboard;

import de.timmi6790.mpstats.api.utilities.BoardUtilities;
import de.timmi6790.mpstats.api.utilities.GameUtilities;
import de.timmi6790.mpstats.api.utilities.LeaderboardUtilities;
import de.timmi6790.mpstats.api.utilities.StatUtilities;
import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.LeaderboardRepository;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractLeaderboardServiceTest {
    private final LeaderboardService leaderboardService;
    private final LeaderboardRepository leaderboardRepository;

    private final GameService gameService;
    private final BoardService boardService;
    private final StatService statService;

    protected AbstractLeaderboardServiceTest(final Supplier<LeaderboardService> leaderboardServiceSupplier,
                                             final GameService gameService,
                                             final StatService statService,
                                             final BoardService boardService) {
        this.leaderboardService = leaderboardServiceSupplier.get();
        this.leaderboardRepository = this.leaderboardService.getLeaderboardRepository();

        this.gameService = gameService;
        this.boardService = boardService;
        this.statService = statService;
    }

    private Game generateGame() {
        return GameUtilities.generateGame(this.gameService);
    }

    private Board generateBoard() {
        return BoardUtilities.generateBoard(this.boardService);
    }

    private Stat generateStat() {
        return StatUtilities.generateStat(this.statService);
    }

    private Leaderboard generateLeaderboard() {
        return LeaderboardUtilities.generateLeaderboard(this.leaderboardService, this.gameService, this.statService, this.boardService);
    }

    private Leaderboard generateLeaderboard(final Game game) {
        return LeaderboardUtilities.generateLeaderboard(this.leaderboardService, this.statService, this.boardService, game);
    }

    private Leaderboard generateLeaderboard(final Game game, final Stat stat) {
        return LeaderboardUtilities.generateLeaderboard(this.leaderboardService, game, stat, this.generateBoard());
    }

    private Leaderboard generateLeaderboard(final Game game, final Board board) {
        return LeaderboardUtilities.generateLeaderboard(
                this.leaderboardService,
                game,
                this.generateStat(),
                board
        );
    }

    private Leaderboard generateLeaderboard(final Stat stat) {
        return LeaderboardUtilities.generateLeaderboard(
                this.leaderboardService,
                this.generateGame(),
                stat,
                this.generateBoard()
        );
    }

    private Leaderboard generateLeaderboard(final Stat stat, final Board board) {
        return LeaderboardUtilities.generateLeaderboard(
                this.leaderboardService,
                this.generateGame(),
                stat,
                board
        );
    }

    private Leaderboard generateLeaderboard(final Board board) {
        return LeaderboardUtilities.generateLeaderboard(
                this.leaderboardService,
                this.generateGame(),
                this.generateStat(),
                board
        );
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
    void getLeaderboards_by_ids() {
        final Map<Integer, Leaderboard> requiredLeaderboards = new HashMap<>();
        for (int count = 0; 10 > count; count++) {
            final Leaderboard leaderboard = this.generateLeaderboard();
            requiredLeaderboards.put(leaderboard.getRepositoryId(), leaderboard);
        }

        final Map<Integer, Leaderboard> foundLeaderboards = this.leaderboardService.getLeaderboards(requiredLeaderboards.keySet());
        assertThat(foundLeaderboards)
                .containsExactlyInAnyOrderEntriesOf(requiredLeaderboards);
    }

    @Test
    void getLeaderboards_game() {
        final Game requiredGame = this.generateGame();
        final List<Leaderboard> requiredLeaderboards = new ArrayList<>();
        for (int count = 0; 10 > count; count++) {
            requiredLeaderboards.add(this.generateLeaderboard(requiredGame));
        }

        // Generate more leaderboards that should not show up in the results
        for (int count = 0; 1 > count; count++) {
            this.generateLeaderboard();
        }

        final List<Leaderboard> foundLeaderboards = this.leaderboardService.getLeaderboards(requiredGame);
        assertThat(foundLeaderboards).containsOnly(requiredLeaderboards.toArray(new Leaderboard[0]));
    }

    @Test
    void getLeaderboards_game_stat() {
        final Game requiredGame = this.generateGame();
        final Stat requiredStat = this.generateStat();
        final List<Leaderboard> requiredLeaderboards = new ArrayList<>();
        for (int count = 0; 10 > count; count++) {
            requiredLeaderboards.add(this.generateLeaderboard(requiredGame, requiredStat));
        }

        // Generate more leaderboards that should not show up in the results
        for (int count = 0; 1 > count; count++) {
            this.generateLeaderboard();
        }

        final List<Leaderboard> foundLeaderboards = this.leaderboardService.getLeaderboards(requiredGame, requiredStat);
        assertThat(foundLeaderboards).containsOnly(requiredLeaderboards.toArray(new Leaderboard[0]));

    }

    @Test
    void getLeaderboards_game_board() {
        final Game requiredGame = this.generateGame();
        final Board requiredBoard = this.generateBoard();
        final List<Leaderboard> requiredLeaderboards = new ArrayList<>();
        for (int count = 0; 10 > count; count++) {
            requiredLeaderboards.add(this.generateLeaderboard(requiredGame, requiredBoard));
        }

        // Generate more leaderboards that should not show up in the results
        for (int count = 0; 1 > count; count++) {
            this.generateLeaderboard();
        }

        final List<Leaderboard> foundLeaderboards = this.leaderboardService.getLeaderboards(requiredGame, requiredBoard);
        assertThat(foundLeaderboards).containsOnly(requiredLeaderboards.toArray(new Leaderboard[0]));

    }

    @Test
    void getLeaderboards_stat() {
        final Stat requiredStat = this.generateStat();
        final List<Leaderboard> requiredLeaderboards = new ArrayList<>();
        for (int count = 0; 10 > count; count++) {
            requiredLeaderboards.add(this.generateLeaderboard(requiredStat));
        }

        // Generate more leaderboards that should not show up in the results
        for (int count = 0; 1 > count; count++) {
            this.generateLeaderboard();
        }

        final List<Leaderboard> foundLeaderboards = this.leaderboardService.getLeaderboards(requiredStat);
        assertThat(foundLeaderboards).containsOnly(requiredLeaderboards.toArray(new Leaderboard[0]));
    }

    @Test
    void getLeaderboards_stat_board() {
        final Stat requiredStat = this.generateStat();
        final Board requiredBoard = this.generateBoard();
        final List<Leaderboard> requiredLeaderboards = new ArrayList<>();
        for (int count = 0; 10 > count; count++) {
            requiredLeaderboards.add(this.generateLeaderboard(requiredStat, requiredBoard));
        }

        // Generate more leaderboards that should not show up in the results
        for (int count = 0; 1 > count; count++) {
            this.generateLeaderboard();
        }

        final List<Leaderboard> foundLeaderboards = this.leaderboardService.getLeaderboards(requiredStat, requiredBoard);
        assertThat(foundLeaderboards).containsOnly(requiredLeaderboards.toArray(new Leaderboard[0]));
    }

    @Test
    void getLeaderboards_board() {
        final Board requiredBoard = this.generateBoard();
        final List<Leaderboard> requiredLeaderboards = new ArrayList<>();
        for (int count = 0; 10 > count; count++) {
            requiredLeaderboards.add(this.generateLeaderboard(requiredBoard));
        }

        // Generate more leaderboards that should not show up in the results
        for (int count = 0; 1 > count; count++) {
            this.generateLeaderboard();
        }

        final List<Leaderboard> foundLeaderboards = this.leaderboardService.getLeaderboards(requiredBoard);
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