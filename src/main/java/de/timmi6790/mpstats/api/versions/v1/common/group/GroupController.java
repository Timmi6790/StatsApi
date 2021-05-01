package de.timmi6790.mpstats.api.versions.v1.common.group;

import de.timmi6790.mpstats.api.security.annontations.RequireAdminPerms;
import de.timmi6790.mpstats.api.versions.v1.common.group.repository.models.Group;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class GroupController {
    private final GroupService groupService;

    protected GroupController(final GroupService groupService) {
        this.groupService = groupService;
    }

    @PutMapping("/{groupName}")
    @RequireAdminPerms
    public Group createGroup(@PathVariable final String groupName, @RequestParam final String cleanName) {
        return null;
    }

    @GetMapping("/{groupName}")
    public Optional<Group> getGroup(@PathVariable final String groupName) {
        return this.groupService.getGroup(groupName);
    }

    @DeleteMapping("/{groupName}")
    @RequireAdminPerms
    public void deleteGroup(@PathVariable final String groupName) {

    }

    @GetMapping
    public List<Group> getGroups() {
        return this.groupService.getGroups();
    }

    @GetMapping("/{groupName}/stat/leaderboard/{stat}/{board}")
    public void getLeaderboard(@PathVariable final String groupName,
                               @PathVariable final String stat,
                               @PathVariable final String board,
                               @RequestParam(required = false, defaultValue = "#{T(java.time.LocalDateTime).now()}")
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateTime,
                               @RequestParam(required = false, defaultValue = "true") final boolean filter) {

    }

    @GetMapping("/{groupName}/stat/player/{playerName}/{stat}/{board}")
    public void getPlayerStats(
            @PathVariable final String groupName,
            @PathVariable final String playerName,
            @PathVariable final String stat,
            @PathVariable final String board,
            @RequestParam(required = false, defaultValue = "#{T(java.time.LocalDateTime).now()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateTime,
            @RequestParam(required = false, defaultValue = "true") final boolean filter) {
        // Include lb, website and generated stats
    }
}
