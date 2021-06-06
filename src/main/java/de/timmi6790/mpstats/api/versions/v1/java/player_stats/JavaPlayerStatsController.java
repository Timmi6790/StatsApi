package de.timmi6790.mpstats.api.versions.v1.java.player_stats;

import de.timmi6790.mpstats.api.versions.v1.common.board.exceptions.InvalidBoardNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.filter.models.Reason;
import de.timmi6790.mpstats.api.versions.v1.common.game.exceptions.InvalidGameNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.exceptions.InvalidLeaderboardCombinationRestException;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.PlayerStatsController;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.models.PlayerStats;
import de.timmi6790.mpstats.api.versions.v1.common.stat.exceptions.InvalidStatNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import de.timmi6790.mpstats.api.versions.v1.common.utilities.RestUtilities;
import de.timmi6790.mpstats.api.versions.v1.java.board.JavaBoardService;
import de.timmi6790.mpstats.api.versions.v1.java.game.JavaGameService;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.JavaLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.java.player.JavaPlayerService;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
import de.timmi6790.mpstats.api.versions.v1.java.stat.JavaStatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/v1/java/player")
@Tag(name = "Java - Player")
public class JavaPlayerStatsController extends PlayerStatsController<JavaPlayer, JavaPlayerService> {
    @Autowired
    public JavaPlayerStatsController(final JavaPlayerStatsService playerStatsService,
                                     final JavaGameService gameService,
                                     final JavaStatService statService,
                                     final JavaBoardService boardService,
                                     final JavaLeaderboardService leaderboardService,
                                     final JavaPlayerService playerService) {
        super(playerStatsService, gameService, statService, boardService, leaderboardService, playerService);
    }

    @GetMapping("/uuid/{playerUUID}/stats/game/{gameName}/{boardName}")
    @Operation(summary = "Find player game stats by player uuid")
    public Optional<PlayerStats<JavaPlayer>> getPlayerGameStats(@PathVariable final UUID playerUUID,
                                                                @PathVariable final String gameName,
                                                                @PathVariable final String boardName,
                                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                                @RequestParam(required = false, defaultValue = "#{T(java.time.ZonedDateTime).now()}") final ZonedDateTime saveTime,
                                                                @RequestParam(required = false, defaultValue = "") final Set<Reason> filterReasons,
                                                                @RequestParam(required = false, defaultValue = "true") final boolean includeEmptyEntries) throws InvalidGameNameRestException, InvalidBoardNameRestException, InvalidLeaderboardCombinationRestException {
        final Game game = RestUtilities.getGameOrThrow(this.getGameService(), gameName);
        final Board board = RestUtilities.getBoardOrThrow(this.getBoardService(), boardName);

        final List<Leaderboard> leaderboards = this.getLeaderboardService().getLeaderboards(game, board);
        if (leaderboards.isEmpty()) {
            final List<Leaderboard> gameLeaderboards = this.getLeaderboardService().getLeaderboards(game);
            throw this.getLeaderboardCombinationRestException(
                    gameLeaderboards,
                    board,
                    game.getGameName(),
                    lb -> lb.getGame().getGameName()
            );
        }

        return this.getPlayerService().getPlayer(playerUUID).flatMap(player ->
                this.getPlayerStatsService().getPlayerStats(
                        leaderboards,
                        player,
                        saveTime,
                        filterReasons,
                        includeEmptyEntries
                )
        );
    }

    @GetMapping("/uuid/{playerUUID}/stats/stat/{statName}/{boardName}")
    @Operation(summary = "Find player stat stats by player uuid")
    public Optional<PlayerStats<JavaPlayer>> getPlayerStatStats(@PathVariable final UUID playerUUID,
                                                                @PathVariable final String statName,
                                                                @PathVariable final String boardName,
                                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                                @RequestParam(required = false, defaultValue = "#{T(java.time.ZonedDateTime).now()}") final ZonedDateTime saveTime,
                                                                @RequestParam(required = false, defaultValue = "") final Set<Reason> filterReasons,
                                                                @RequestParam(required = false, defaultValue = "true") final boolean includeEmptyEntries) throws InvalidStatNameRestException, InvalidBoardNameRestException, InvalidLeaderboardCombinationRestException {
        final Stat stat = RestUtilities.getStatOrThrow(this.getStatService(), statName);
        final Board board = RestUtilities.getBoardOrThrow(this.getBoardService(), boardName);

        final List<Leaderboard> leaderboards = this.getLeaderboardService().getLeaderboards(stat, board);
        if (leaderboards.isEmpty()) {
            final List<Leaderboard> statLeaderboards = this.getLeaderboardService().getLeaderboards(stat);
            throw this.getLeaderboardCombinationRestException(
                    statLeaderboards,
                    board,
                    stat.getStatName(),
                    lb -> lb.getStat().getStatName()
            );
        }

        return this.getPlayerService().getPlayer(playerUUID).flatMap(player ->
                this.getPlayerStatsService().getPlayerStats(
                        leaderboards,
                        player,
                        saveTime,
                        filterReasons,
                        includeEmptyEntries
                )
        );
    }
}
