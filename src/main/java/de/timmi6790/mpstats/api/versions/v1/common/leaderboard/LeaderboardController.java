package de.timmi6790.mpstats.api.versions.v1.common.leaderboard;

import de.timmi6790.mpstats.api.security.annontations.RequireAdminPerms;
import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Getter(AccessLevel.PROTECTED)
public abstract class LeaderboardController {
    private final GameService gameService;
    private final StatService statService;
    private final BoardService boardService;
    private final LeaderboardService leaderboardService;

    protected LeaderboardController(final LeaderboardService leaderboardService,
                                    final GameService gameService,
                                    final StatService statService,
                                    final BoardService boardService) {
        this.leaderboardService = leaderboardService;
        this.gameService = gameService;
        this.statService = statService;
        this.boardService = boardService;
    }

    @GetMapping
    @Operation(summary = "Find all available leaderboards")
    public List<Leaderboard> getLeaderboards() {
        return this.leaderboardService.getLeaderboards();
    }

    @GetMapping("/{gameName}/{statName}/{boardName}")
    @Operation(summary = "Find leaderboard by name")
    public Optional<Leaderboard> getLeaderboard(@PathVariable final String gameName,
                                                @PathVariable final String statName,
                                                @PathVariable final String boardName) {
        final Optional<Game> gameOpt = this.gameService.getGame(gameName);
        final Optional<Stat> statOpt = this.statService.getStat(statName);
        final Optional<Board> boardOpt = this.boardService.getBoard(boardName);

        if (gameOpt.isPresent() && statOpt.isPresent() && boardOpt.isPresent()) {
            return this.leaderboardService.getLeaderboard(gameOpt.get(), statOpt.get(), boardOpt.get());
        }
        return Optional.empty();
    }

    @PutMapping("/{gameName}/{statName}/{boardName}")
    @Operation(summary = "Create a new leaderboard")
    @RequireAdminPerms
    public Optional<Leaderboard> createdLeaderboard(@PathVariable final String gameName,
                                                    @PathVariable final String statName,
                                                    @PathVariable final String boardName,
                                                    @RequestParam final boolean deprecated) {
        final Optional<Game> gameOpt = this.gameService.getGame(gameName);
        final Optional<Stat> statOpt = this.statService.getStat(statName);
        final Optional<Board> boardOpt = this.boardService.getBoard(boardName);

        if (gameOpt.isPresent() && statOpt.isPresent() && boardOpt.isPresent()) {
            return Optional.of(
                    this.leaderboardService.getLeaderboardOrCreate(
                            gameOpt.get(),
                            statOpt.get(),
                            boardOpt.get(),
                            deprecated
                    )
            );
        }
        return Optional.empty();
    }
}
