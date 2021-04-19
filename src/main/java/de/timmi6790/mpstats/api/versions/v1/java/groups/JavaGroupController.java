package de.timmi6790.mpstats.api.versions.v1.java.groups;

import de.timmi6790.mpstats.api.versions.v1.java.groups.repository.models.Group;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/java/group")
@Tag(name = "Java - Group")
public class JavaGroupController {
    private final JavaGroupService javaGroupService;

    @Autowired
    public JavaGroupController(final JavaGroupService javaGroupService) {
        this.javaGroupService = javaGroupService;
    }

    @PutMapping(value = "/{groupName}")
    public Group createGroup(@PathVariable final String groupName, @RequestParam final String cleanName) {
        return null;
    }

    @GetMapping(value = "/{groupName}")
    public Optional<Group> getGroup(@PathVariable final String groupName) {
        return Optional.empty();
    }

    @DeleteMapping(value = "/{groupName}")
    public void deleteGroup(@PathVariable final String groupName) {

    }

    @GetMapping
    public List<Group> getGroups() {
        return this.javaGroupService.getGroups();
    }

    @GetMapping(value = "/{groupName}/stat/leaderboard/{stat}/{board}")
    public void getLeaderboard(@PathVariable final String groupName,
                               @PathVariable final String stat,
                               @PathVariable final String board,
                               @RequestParam(required = false, defaultValue = "#{T(java.time.LocalDateTime).now()}")
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateTime,
                               @RequestParam(required = false, defaultValue = "true") final boolean filter) {

    }

    @GetMapping(value = "/{groupName}/stat/player/{playerName}/{stat}/{board}")
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
