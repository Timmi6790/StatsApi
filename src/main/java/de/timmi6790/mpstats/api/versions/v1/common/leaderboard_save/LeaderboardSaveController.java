package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save;

import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.board.exceptions.InvalidBoardNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.game.exceptions.InvalidGameNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.exceptions.InvalidLeaderboardCombinationRestException;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import de.timmi6790.mpstats.api.versions.v1.common.stat.exceptions.InvalidStatNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.utilities.RestUtilities;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.ZonedDateTime;
import java.util.List;

@Getter(AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class LeaderboardSaveController<P extends Player> {
    private final GameService gameService;
    private final StatService statService;
    private final BoardService boardService;
    private final LeaderboardService leaderboardService;
    private final LeaderboardSaveService<P> leaderboardSaveService;

    @GetMapping("/{gameName}/{statName}/{boardName}/saves")
    @Operation(summary = "Find leaderboard save times")
    public List<ZonedDateTime> getLeaderboardSaveTimes(@PathVariable final String gameName,
                                                       @PathVariable final String statName,
                                                       @PathVariable final String boardName) throws InvalidStatNameRestException, InvalidGameNameRestException, InvalidBoardNameRestException, InvalidLeaderboardCombinationRestException {
        final Leaderboard leaderboard = RestUtilities.getLeaderboardOrThrow(
                this.gameService,
                gameName,
                this.statService,
                statName,
                this.boardService,
                boardName,
                this.leaderboardService
        );

        return this.leaderboardSaveService.getLeaderboardSaveTimes(leaderboard);
    }
}
