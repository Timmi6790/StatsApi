package de.timmi6790.mpstats.api.versions.v1.java.player_stats;

import de.timmi6790.mpstats.api.versions.v1.common.player_stats.PlayerStatsController;
import de.timmi6790.mpstats.api.versions.v1.java.board.JavaBoardService;
import de.timmi6790.mpstats.api.versions.v1.java.game.JavaGameService;
import de.timmi6790.mpstats.api.versions.v1.java.player.JavaPlayerService;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
import de.timmi6790.mpstats.api.versions.v1.java.stat.JavaStatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/java/player")
@Tag(name = "Java - Player")
public class JavaPlayerStatsController extends PlayerStatsController<JavaPlayer, JavaPlayerService> {
    @Autowired
    public JavaPlayerStatsController(final JavaPlayerStatsService playerStatsService,
                                     final JavaGameService gameService,
                                     final JavaStatService statService,
                                     final JavaBoardService boardService,
                                     final JavaPlayerService playerService) {
        super(playerStatsService, gameService, statService, boardService, playerService);
    }
}
