package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder;

import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.board.exceptions.InvalidBoardNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.filter.models.Reason;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.game.exceptions.InvalidGameNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.exceptions.InvalidLeaderboardCombinationRestException;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardPositionSave;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import de.timmi6790.mpstats.api.versions.v1.common.stat.exceptions.InvalidStatNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.utilities.RestUtilities;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

@Getter(AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class LeaderboardSaveCombinerController<P extends Player, S extends PlayerService<P>> {
    private final LeaderboardSaveCombinerService<P, S> leaderboardSaveCombinerService;

    private final GameService gameService;
    private final StatService statService;
    private final BoardService boardService;
    private final LeaderboardService leaderboardService;

    @GetMapping("/{gameName}/{statName}/{boardName}/save")
    @Operation(summary = "Find leaderboard saves")
    public Optional<LeaderboardPositionSave<P>> getLeaderboardSave(@PathVariable final String gameName,
                                                                   @PathVariable final String statName,
                                                                   @PathVariable final String boardName,
                                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                                   @RequestParam(required = false, defaultValue = "#{T(java.time.ZonedDateTime).now()}") final ZonedDateTime saveTime,
                                                                   @RequestParam(required = false) final Set<Reason> filterReasons) throws InvalidGameNameRestException, InvalidStatNameRestException, InvalidBoardNameRestException, InvalidLeaderboardCombinationRestException {
        final Leaderboard leaderboard = RestUtilities.getLeaderboardOrThrow(
                this.gameService,
                gameName,
                this.statService,
                statName,
                this.boardService,
                boardName,
                this.leaderboardService
        );

        if (filterReasons == null || filterReasons.isEmpty()) {
            return this.leaderboardSaveCombinerService.getLeaderboardSave(leaderboard, saveTime);
        } else {
            return this.leaderboardSaveCombinerService.getLeaderboardSave(leaderboard, saveTime, filterReasons);
        }
    }
}
