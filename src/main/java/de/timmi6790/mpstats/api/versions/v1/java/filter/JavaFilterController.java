package de.timmi6790.mpstats.api.versions.v1.java.filter;

import de.timmi6790.mpstats.api.versions.v1.common.filter.FilterController;
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
}
