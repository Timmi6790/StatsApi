package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter(AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class LeaderboardSaveController<P extends Player> {
    private final LeaderboardService leaderboardService;
    private final LeaderboardSaveService<P> leaderboardSaveService;

    @GetMapping("/{gameName}/{statName}/{boardName}/saves")
    @Operation(summary = "Find leaderboard save times")
    public Optional<List<LocalDateTime>> getLeaderboardSaveTimes(@PathVariable final String gameName,
                                                                 @PathVariable final String statName,
                                                                 @PathVariable final String boardName) {
        return this.leaderboardService.getLeaderboard(gameName, statName, boardName)
                .map(this.leaderboardSaveService::getLeaderboardSaveTimes);
    }
}
