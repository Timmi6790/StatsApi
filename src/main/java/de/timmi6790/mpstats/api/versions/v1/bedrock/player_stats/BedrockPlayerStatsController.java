package de.timmi6790.mpstats.api.versions.v1.bedrock.player_stats;

import de.timmi6790.mpstats.api.versions.v1.bedrock.board.BedrockBoardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.game.BedrockGameService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard.BedrockLeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.BedrockPlayerService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockPlayer;
import de.timmi6790.mpstats.api.versions.v1.bedrock.stat.BedrockStatService;
import de.timmi6790.mpstats.api.versions.v1.common.filter.models.Reason;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.player.exceptions.InvalidPlayerNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.PlayerStatsController;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.models.PlayerStats;
import de.timmi6790.mpstats.api.versions.v1.common.utilities.RestUtilities;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/v1/bedrock/player")
@Tag(name = "Bedrock - Player")
public class BedrockPlayerStatsController extends PlayerStatsController<BedrockPlayer, BedrockPlayerService> {
    @Autowired
    public BedrockPlayerStatsController(final BedrockPlayerStatsService playerStatsService,
                                        final BedrockGameService gameService,
                                        final BedrockStatService statService,
                                        final BedrockBoardService boardService,
                                        final BedrockLeaderboardService leaderboardService,
                                        final BedrockPlayerService playerService) {
        super(playerStatsService, gameService, statService, boardService, leaderboardService, playerService);
    }

    @GetMapping("{playerName}/stats/game")
    @Operation(summary = "Find player stats for all leaderboards")
    public Optional<PlayerStats<BedrockPlayer>> getPlayerStatsAll(@PathVariable final String playerName,
                                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                                  @RequestParam(required = false, defaultValue = "#{T(java.time.ZonedDateTime).now()}") final ZonedDateTime saveTime,
                                                                  @RequestParam(required = false, defaultValue = "") final Set<Reason> filterReasons,
                                                                  @RequestParam(required = false, defaultValue = "true") final boolean includeEmptyEntries) throws InvalidPlayerNameRestException {
        RestUtilities.verifyPlayerName(this.getPlayerService(), playerName);

        final List<Leaderboard> leaderboards = this.getLeaderboardService().getLeaderboards();
        if (leaderboards.isEmpty()) {
            return Optional.empty();
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
