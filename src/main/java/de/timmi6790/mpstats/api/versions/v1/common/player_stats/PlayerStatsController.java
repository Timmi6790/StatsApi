package de.timmi6790.mpstats.api.versions.v1.common.player_stats;

import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.board.exceptions.InvalidBoardNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.filter.models.Reason;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.game.exceptions.InvalidGameNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.exceptions.InvalidLeaderboardCombinationRestException;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.exceptions.InvalidPlayerNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.models.PlayerStats;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import de.timmi6790.mpstats.api.versions.v1.common.stat.exceptions.InvalidStatNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import de.timmi6790.mpstats.api.versions.v1.common.utilities.RestUtilities;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@AllArgsConstructor
public class PlayerStatsController<P extends Player, S extends PlayerService<P>> {
    private final PlayerStatsService<P, S> playerStatsService;
    private final GameService gameService;
    private final StatService statService;
    private final BoardService boardService;
    private final LeaderboardService leaderboardService;
    private final S playerService;

    private Optional<PlayerStats<P>> getPlayerStats(final List<Leaderboard> leaderboards,
                                                    final String playerName,
                                                    final ZonedDateTime saveTime,
                                                    final Set<Reason> filterReasons,
                                                    final boolean includeEmptyEntries) {
        return this.playerService.getPlayer(playerName).flatMap(player ->
                this.playerStatsService.getPlayerStats(
                        leaderboards,
                        player,
                        saveTime,
                        filterReasons,
                        includeEmptyEntries
                )
        );
    }

    private InvalidLeaderboardCombinationRestException getLeaderboardCombinationRestException(final List<Leaderboard> firstLeaderboards,
                                                                                              final Board board,
                                                                                              final String secondCompareValue,
                                                                                              final Function<Leaderboard, String> secondToStringFunction) {
        if (!firstLeaderboards.isEmpty()) {
            return new InvalidLeaderboardCombinationRestException(
                    RestUtilities.getSimilarValues(
                            board.getBoardName(),
                            firstLeaderboards,
                            lb -> lb.getBoard().getBoardName()
                    )
            );
        }

        final List<Leaderboard> boardLeaderboards = this.leaderboardService.getLeaderboards(board);
        if (!boardLeaderboards.isEmpty()) {
            return new InvalidLeaderboardCombinationRestException(
                    RestUtilities.getSimilarValues(
                            secondCompareValue,
                            boardLeaderboards,
                            secondToStringFunction
                    )
            );
        }

        return new InvalidLeaderboardCombinationRestException(Collections.emptyList());
    }

    @GetMapping("{playerName}/stats/game/{gameName}/{boardName}")
    @Operation(summary = "Find player game stats")
    public Optional<PlayerStats<P>> getPlayerGameStats(@PathVariable final String playerName,
                                                       @PathVariable final String gameName,
                                                       @PathVariable final String boardName,
                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                       @RequestParam(required = false, defaultValue = "#{T(java.time.ZonedDateTime).now()}") final ZonedDateTime saveTime,
                                                       @RequestParam(required = false, defaultValue = "") final Set<Reason> filterReasons,
                                                       @RequestParam(required = false, defaultValue = "true") final boolean includeEmptyEntries) throws InvalidGameNameRestException, InvalidBoardNameRestException, InvalidPlayerNameRestException, InvalidLeaderboardCombinationRestException {
        RestUtilities.verifyPlayerName(this.playerService, playerName);
        final Game game = RestUtilities.getGameOrThrow(this.gameService, gameName);
        final Board board = RestUtilities.getBoardOrThrow(this.boardService, boardName);

        final List<Leaderboard> leaderboards = this.leaderboardService.getLeaderboards(game, board);
        if (leaderboards.isEmpty()) {
            final List<Leaderboard> gameLeaderboards = this.leaderboardService.getLeaderboards(game);
            throw this.getLeaderboardCombinationRestException(
                    gameLeaderboards,
                    board,
                    game.getGameName(),
                    lb -> lb.getGame().getGameName()
            );
        }

        return this.getPlayerStats(
                leaderboards,
                playerName,
                saveTime,
                filterReasons,
                includeEmptyEntries
        );
    }

    @GetMapping("{playerName}/stats/stat/{statName}/{boardName}")
    @Operation(summary = "Find player stat stats")
    public Optional<PlayerStats<P>> getPlayerStatStats(@PathVariable final String playerName,
                                                       @PathVariable final String statName,
                                                       @PathVariable final String boardName,
                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                       @RequestParam(required = false, defaultValue = "#{T(java.time.ZonedDateTime).now()}") final ZonedDateTime saveTime,
                                                       @RequestParam(required = false, defaultValue = "") final Set<Reason> filterReasons,
                                                       @RequestParam(required = false, defaultValue = "true") final boolean includeEmptyEntries) throws InvalidStatNameRestException, InvalidBoardNameRestException, InvalidPlayerNameRestException, InvalidLeaderboardCombinationRestException {
        RestUtilities.verifyPlayerName(this.playerService, playerName);
        final Stat stat = RestUtilities.getStatOrThrow(this.statService, statName);
        final Board board = RestUtilities.getBoardOrThrow(this.boardService, boardName);

        final List<Leaderboard> leaderboards = this.leaderboardService.getLeaderboards(stat, board);
        if (leaderboards.isEmpty()) {
            final List<Leaderboard> statLeaderboards = this.leaderboardService.getLeaderboards(stat);
            throw this.getLeaderboardCombinationRestException(
                    statLeaderboards,
                    board,
                    stat.getStatName(),
                    lb -> lb.getStat().getStatName()
            );
        }

        return this.getPlayerStats(
                leaderboards,
                playerName,
                saveTime,
                filterReasons,
                includeEmptyEntries
        );
    }
}
