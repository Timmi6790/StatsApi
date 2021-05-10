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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static de.timmi6790.mpstats.api.utilities.BoardUtilities.generateBoardName;
import static de.timmi6790.mpstats.api.utilities.GameUtilities.generateGameName;
import static de.timmi6790.mpstats.api.utilities.StatUtilities.generateStatName;
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
        for (int count = 0; 1 > count; count++) {
            this.generateLeaderboard();
        }

        final List<Leaderboard> foundLeaderboards = this.leaderboardService.getLeaderboards(requiredGame);
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
    void getLeaderboard_by_names() {
        final Game game = this.generateGame();
        final Stat stat = this.generateStat();
        final Board board = this.generateBoard();
        final boolean deprecated = true;

        // Create leaderboard
        final Leaderboard leaderboard = this.leaderboardService.getLeaderboardOrCreate(game, stat, board, deprecated);

        final Optional<Leaderboard> leaderboardFound = this.leaderboardService.getLeaderboard(
                game.getGameName(),
                stat.getStatName(),
                board.getBoardName()
        );
        assertThat(leaderboardFound)
                .isPresent()
                .contains(leaderboard);
    }

    @Test
    void getLeaderboard_by_names_invalid_game_name() {
        final Game game = this.generateGame();
        final Stat stat = this.generateStat();
        final Board board = this.generateBoard();
        final boolean deprecated = true;

        // Create leaderboard
        this.leaderboardService.getLeaderboardOrCreate(game, stat, board, deprecated);

        final String uniqGameName = generateGameName();
        final Optional<Leaderboard> leaderboardNotFound = this.leaderboardService.getLeaderboard(
                uniqGameName,
                stat.getStatName(),
                board.getBoardName()
        );
        assertThat(leaderboardNotFound)
                .isNotPresent();
    }

    @Test
    void getLeaderboard_by_names_invalid_stat_name() {
        final Game game = this.generateGame();
        final Stat stat = this.generateStat();
        final Board board = this.generateBoard();
        final boolean deprecated = true;

        // Create leaderboard
        this.leaderboardService.getLeaderboardOrCreate(game, stat, board, deprecated);

        final String uniqStatName = generateStatName();
        final Optional<Leaderboard> leaderboardNotFound = this.leaderboardService.getLeaderboard(
                game.getGameName(),
                uniqStatName,
                board.getBoardName()
        );
        assertThat(leaderboardNotFound)
                .isNotPresent();
    }

    @Test
    void getLeaderboard_by_names_invalid_board_name() {
        final Game game = this.generateGame();
        final Stat stat = this.generateStat();
        final Board board = this.generateBoard();
        final boolean deprecated = true;

        // Create leaderboard
        this.leaderboardService.getLeaderboardOrCreate(game, stat, board, deprecated);

        final String uniqBoardName = generateBoardName();
        final Optional<Leaderboard> leaderboardNotFound = this.leaderboardService.getLeaderboard(
                game.getGameName(),
                stat.getStatName(),
                uniqBoardName
        );
        assertThat(leaderboardNotFound)
                .isNotPresent();
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