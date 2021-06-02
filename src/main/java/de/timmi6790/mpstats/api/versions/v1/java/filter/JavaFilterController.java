package de.timmi6790.mpstats.api.versions.v1.java.filter;

import de.timmi6790.mpstats.api.security.annontations.RequireAdminPerms;
import de.timmi6790.mpstats.api.versions.v1.common.board.exceptions.InvalidBoardNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.filter.FilterController;
import de.timmi6790.mpstats.api.versions.v1.common.filter.models.Reason;
import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models.Filter;
import de.timmi6790.mpstats.api.versions.v1.common.game.exceptions.InvalidGameNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.exceptions.InvalidLeaderboardCombinationRestException;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.stat.exceptions.InvalidStatNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.utilities.RestUtilities;
import de.timmi6790.mpstats.api.versions.v1.java.board.JavaBoardService;
import de.timmi6790.mpstats.api.versions.v1.java.game.JavaGameService;
import de.timmi6790.mpstats.api.versions.v1.java.player.JavaPlayerService;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
import de.timmi6790.mpstats.api.versions.v1.java.stat.JavaStatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/v1/java/filter")
@Tag(name = "Java - Filter")
public class JavaFilterController extends FilterController<JavaPlayer, JavaPlayerService> {
    @Autowired
    public JavaFilterController(final JavaGameService gameService,
                                final JavaStatService statService,
                                final JavaBoardService boardService,
                                final JavaFilterService filterService) {
        super(gameService, statService, boardService, filterService);
    }

    @PostMapping("/{gameName}/{statName}/{boardName}/{playerUUID}")
    @Operation(summary = "Create a new filter")
    @RequireAdminPerms
    public Optional<Filter<JavaPlayer>> createFilter(@PathVariable final String gameName,
                                                     @PathVariable final String statName,
                                                     @PathVariable final String boardName,
                                                     @PathVariable final UUID playerUUID,
                                                     @RequestParam final Reason reason,
                                                     @RequestParam final ZonedDateTime filterStart,
                                                     @RequestParam final ZonedDateTime filterEnd) throws InvalidLeaderboardCombinationRestException, InvalidStatNameRestException, InvalidBoardNameRestException, InvalidGameNameRestException {
        final Leaderboard leaderboard = RestUtilities.getLeaderboardOrThrow(
                this.getGameService(),
                gameName,
                this.getStatService(),
                statName,
                this.getBoardService(),
                boardName,
                this.getLeaderboardService()
        );

        final Optional<JavaPlayer> playerOpt = this.getPlayerService().getPlayer(playerUUID);
        if (playerOpt.isEmpty()) {
            return Optional.empty();
        }

        final Filter<JavaPlayer> filter = this.getFilterService().addFilter(
                playerOpt.get(),
                leaderboard,
                reason,
                filterStart,
                filterEnd
        );
        return Optional.of(filter);
    }
}
