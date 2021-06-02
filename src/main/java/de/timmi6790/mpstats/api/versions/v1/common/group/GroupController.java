package de.timmi6790.mpstats.api.versions.v1.common.group;

import de.timmi6790.mpstats.api.security.annontations.RequireAdminPerms;
import de.timmi6790.mpstats.api.versions.v1.common.filter.models.Reason;
import de.timmi6790.mpstats.api.versions.v1.common.group.repository.models.Group;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.PlayerStatsService;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.models.PlayerStats;
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
public class GroupController<P extends Player> {
    private final GroupService groupService;
    private final PlayerStatsService playerStatsService;

    @PutMapping("/{groupName}")
    @RequireAdminPerms
    public Group createGroup(@PathVariable final String groupName, @RequestParam final String cleanName) {
        throw new UnsupportedOperationException();
    }

    @GetMapping("/{groupName}")
    public Optional<Group> getGroup(@PathVariable final String groupName) {
        return this.groupService.getGroup(groupName);
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

    @GetMapping("/{groupName}/stat/player/{playerName}/{stat}/{board}")
    public Optional<PlayerStats<P>> getPlayerStats(
            @PathVariable final String groupName,
            @PathVariable final String playerName,
            @PathVariable final String stat,
            @PathVariable final String board,
            @RequestParam(required = false, defaultValue = "#{T(java.time.ZonedDateTime).now()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final ZonedDateTime dateTime,
            @RequestParam(required = false, defaultValue = "") final Set<Reason> filterReasons,
            @RequestParam(required = false, defaultValue = "true") final boolean includeEmptyEntries) {
        final Optional<Group> groupOpt = this.groupService.getGroup(groupName);
        throw new UnsupportedOperationException();
    }
}
