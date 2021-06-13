package de.timmi6790.mpstats.api.versions.v1.common.group;

import com.google.common.collect.Lists;
import de.timmi6790.mpstats.api.security.annontations.RequireAdminPerms;
import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.board.exceptions.InvalidBoardNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.filter.models.Reason;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.group.exceptions.InvalidGroupNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.group.repository.models.Group;
import de.timmi6790.mpstats.api.versions.v1.common.group.repository.models.GroupPlayerStats;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.exceptions.InvalidPlayerNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.PlayerStatsService;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import de.timmi6790.mpstats.api.versions.v1.common.stat.exceptions.InvalidStatNameRestException;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import de.timmi6790.mpstats.api.versions.v1.common.utilities.RestUtilities;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Getter(AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
// TODO: FINISH ME
public class GroupController<P extends Player, S extends PlayerService<P>> {
    private final GroupService groupService;
    private final PlayerService<P> playerService;
    private final StatService statService;
    private final BoardService boardService;
    private final LeaderboardService leaderboardService;
    private final PlayerStatsService<P, S> playerStatsService;

    @PutMapping("/{groupName}")
    @RequireAdminPerms
    public Group createGroup(@PathVariable final String groupName, @RequestParam final String cleanName) {
        throw new UnsupportedOperationException();
    }

    @GetMapping("/{groupName}")
    public Group getGroup(@PathVariable final String groupName) throws InvalidGroupNameRestException {
        return RestUtilities.getGroupOrThrow(this.groupService, groupName);
    }

    @DeleteMapping("/{groupName}")
    @RequireAdminPerms
    public void deleteGroup(@PathVariable final String groupName) {
        throw new UnsupportedOperationException();
    }

    @GetMapping
    public List<Group> getGroups() {
        return this.groupService.getGroups();
    }

    @GetMapping("/{groupName}/stat/player/{playerName}/{statName}/{boardName}")
    public Optional<GroupPlayerStats<P>> getPlayerStats(
            @PathVariable final String groupName,
            @PathVariable final String playerName,
            @PathVariable final String statName,
            @PathVariable final String boardName,
            @RequestParam(required = false, defaultValue = "#{T(java.time.ZonedDateTime).now()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final ZonedDateTime saveTime,
            @RequestParam(required = false, defaultValue = "") final Set<Reason> filterReasons,
            @RequestParam(required = false, defaultValue = "true") final boolean includeEmptyEntries) throws InvalidGroupNameRestException, InvalidPlayerNameRestException, InvalidStatNameRestException, InvalidBoardNameRestException {
        RestUtilities.verifyPlayerName(this.playerService, playerName);

        final Stat stat = RestUtilities.getStatOrThrow(this.statService, statName);
        final Board board = RestUtilities.getBoardOrThrow(this.boardService, boardName);
        final Group group = RestUtilities.getGroupOrThrow(this.groupService, groupName);

        final List<Leaderboard> leaderboards = Lists.newArrayListWithExpectedSize(group.getGames().size());
        for (final Game game : group.getGames()) {
            this.leaderboardService.getLeaderboard(game, stat, board).ifPresent(leaderboards::add);
        }

        if (leaderboards.isEmpty()) {
            // TODO: Throw exception
            return Optional.empty();
        }

        return this.getPlayerService().getPlayer(playerName).flatMap(player ->
                this.getPlayerStatsService().getPlayerStats(
                        leaderboards,
                        player,
                        saveTime,
                        filterReasons,
                        includeEmptyEntries
                )
        ).map(playerStats -> new GroupPlayerStats<>(playerStats, group));
    }
}
