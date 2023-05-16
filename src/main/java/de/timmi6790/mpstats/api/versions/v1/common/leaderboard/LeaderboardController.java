package de.timmi6790.mpstats.api.versions.v1.common.leaderboard;

import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.board.exceptions.InvalidBoardNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.game.exceptions.InvalidGameNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.exceptions.InvalidLeaderboardCombinationRestException;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import de.timmi6790.mpstats.api.versions.v1.common.stat.exceptions.InvalidStatNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import de.timmi6790.mpstats.api.versions.v1.common.utilities.RestUtilities;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Getter(AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class LeaderboardController {
    private final LeaderboardService leaderboardService;
    private final GameService gameService;
    private final StatService statService;
    private final BoardService boardService;

    @GetMapping
    @Operation(summary = "Find all available leaderboards")
    public List<Leaderboard> getLeaderboards() {
        return this.leaderboardService.getLeaderboards();
    }

    @GetMapping("/{gameName}")
    @Operation(summary = "Find leaderboard by game name")
    public List<Leaderboard> getLeaderboards(@PathVariable final String gameName) throws InvalidGameNameRestException {
        final Game game = RestUtilities.getGameOrThrow(this.gameService, gameName);
        return this.leaderboardService.getLeaderboards(game);
    }

    @GetMapping("/{gameName}/{statName}")
    @Operation(summary = "Find leaderboard by game and stat name")
    public List<Leaderboard> getLeaderboards(@PathVariable final String gameName,
                                             @PathVariable final String statName) throws InvalidGameNameRestException, InvalidStatNameRestException {
        final Game game = RestUtilities.getGameOrThrow(this.gameService, gameName);
        final Stat stat = RestUtilities.getStatOrThrow(this.statService, statName);
        return this.leaderboardService.getLeaderboards(game, stat);
    }

    @GetMapping("/{gameName}/{statName}/{boardName}")
    @Operation(summary = "Find leaderboard by name")
    public Leaderboard getLeaderboard(@PathVariable final String gameName,
                                      @PathVariable final String statName,
                                      @PathVariable final String boardName) throws InvalidGameNameRestException, InvalidStatNameRestException, InvalidBoardNameRestException, InvalidLeaderboardCombinationRestException {
        return RestUtilities.getLeaderboardOrThrow(
                this.gameService,
                gameName,
                this.statService,
                statName,
                this.boardService,
                boardName,
                this.leaderboardService
        );
    }
}
