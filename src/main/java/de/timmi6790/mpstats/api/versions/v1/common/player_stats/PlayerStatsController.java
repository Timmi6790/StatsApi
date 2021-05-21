package de.timmi6790.mpstats.api.versions.v1.common.player_stats;

import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.filter.models.Reason;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.models.PlayerStats;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
public class PlayerStatsController<P extends Player, S extends PlayerService<P>> {
    private final PlayerStatsService<P, S> playerStatsService;
    private final GameService gameService;
    private final StatService statService;
    private final BoardService boardService;
    private final S playerService;

    @GetMapping("{playerName}/stats/game/{gameName}/{boardName}")
    @Operation(summary = "Find player game stats")
    public Optional<PlayerStats<P>> getPlayerGameStats(@PathVariable final String playerName,
                                                       @PathVariable final String gameName,
                                                       @PathVariable final String boardName,
                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam(required = false, defaultValue = "#{T(java.time.ZonedDateTime).now()}") final ZonedDateTime saveTime,
                                                       @RequestParam(required = false, defaultValue = "") final Set<Reason> filterReasons,
                                                       @RequestParam(required = false, defaultValue = "true") final boolean includeEmptyEntries) {
        final Optional<Game> gameOpt = this.gameService.getGame(gameName);
        if (gameOpt.isEmpty()) {
            return Optional.empty();
        }

        final Optional<Board> boardOpt = this.boardService.getBoard(boardName);
        if (boardOpt.isEmpty()) {
            return Optional.empty();
        }

        final Optional<P> playerOpt = this.playerService.getPlayer(playerName);
        if (playerOpt.isEmpty()) {
            return Optional.empty();
        }

        return this.playerStatsService.getPlayerGameStats(
                playerOpt.get(),
                gameOpt.get(),
                boardOpt.get(),
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
                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam(required = false, defaultValue = "#{T(java.time.ZonedDateTime).now()}") final ZonedDateTime saveTime,
                                                       @RequestParam(required = false, defaultValue = "") final Set<Reason> filterReasons,
                                                       @RequestParam(required = false, defaultValue = "true") final boolean includeEmptyEntries) {
        final Optional<Stat> statOpt = this.statService.getStat(statName);
        if (statOpt.isEmpty()) {
            return Optional.empty();
        }

        final Optional<Board> boardOpt = this.boardService.getBoard(boardName);
        if (boardOpt.isEmpty()) {
            return Optional.empty();
        }

        final Optional<P> playerOpt = this.playerService.getPlayer(playerName);
        if (playerOpt.isEmpty()) {
            return Optional.empty();
        }

        return this.playerStatsService.getPlayerStatStats(
                playerOpt.get(),
                statOpt.get(),
                boardOpt.get(),
                saveTime,
                filterReasons,
                includeEmptyEntries
        );
    }
}
