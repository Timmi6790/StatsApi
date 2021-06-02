package de.timmi6790.mpstats.api.versions.v1.java.group;

import de.timmi6790.mpstats.api.versions.v1.common.group.GroupController;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
import de.timmi6790.mpstats.api.versions.v1.java.player_stats.JavaPlayerStatsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/java/group")
@Tag(name = "Java - Group")
public class JavaGroupController extends GroupController<JavaPlayer> {
    @Autowired
    public JavaGroupController(final JavaGroupService javaGroupService, final JavaPlayerStatsService playerStatsService) {
        super(javaGroupService, playerStatsService);
    }
}
