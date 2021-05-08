package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder;

import de.timmi6790.mpstats.api.versions.v1.common.filter.models.Reason;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardPositionSave;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Getter(AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class LeaderboardSaveCombinerController<P extends Player, S extends PlayerService<P>> {
    private final LeaderboardSaveCombinerService<P, S> leaderboardSaveCombinerService;
    private final LeaderboardService leaderboardService;

    @GetMapping("/{gameName}/{statName}/{boardName}/save")
    @Operation(summary = "Find leaderboard saves")
    public Optional<LeaderboardPositionSave<P>> getLeaderboardSave(@PathVariable final String gameName,
                                                                   @PathVariable final String statName,
                                                                   @PathVariable final String boardName,
                                                                   @RequestParam(required = false, defaultValue = "#{T(java.time.ZonedDateTime).now()}") final ZonedDateTime saveTime,
                                                                   @RequestParam(required = false) final List<Reason> filterReasons) {
        if (filterReasons == null || filterReasons.isEmpty()) {
            return this.leaderboardService.getLeaderboard(gameName, statName, boardName)
                    .flatMap(leaderboard -> this.leaderboardSaveCombinerService.getLeaderboardSave(leaderboard, saveTime));
        } else {
            return this.leaderboardService.getLeaderboard(gameName, statName, boardName)
                    .flatMap(leaderboard -> this.leaderboardSaveCombinerService.getLeaderboardSave(leaderboard, saveTime, filterReasons));
        }
    }

}
