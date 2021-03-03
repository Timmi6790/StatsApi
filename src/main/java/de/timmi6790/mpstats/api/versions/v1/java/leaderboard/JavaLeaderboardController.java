package de.timmi6790.mpstats.api.versions.v1.java.leaderboard;

import de.timmi6790.mpstats.api.configs.OpenApiConfig;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.models.JavaLeaderboardModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/java/leaderboard/")
@Tag(name = OpenApiConfig.TAG_JAVA)
public class JavaLeaderboardController {
    private final JavaLeaderboardService leaderboardService;

    @Autowired
    public JavaLeaderboardController(final JavaLeaderboardService javaLeaderboardService) {
        this.leaderboardService = javaLeaderboardService;
    }

    @GetMapping(value = "{game}/{stat}/{board}")
    public List<JavaLeaderboardModel> getLeaderboard(
            @PathVariable final String game,
            @PathVariable final String stat,
            @PathVariable final String board,
            @RequestParam(required = false, defaultValue = "1") @Min(1) @Max(1_000) final int startPosition,
            @RequestParam(required = false, defaultValue = "1000") @Min(1) @Max(1_000) final int endPosition,
            @RequestParam(required = false, defaultValue = "true") final boolean filter,
            @RequestParam(required = false, defaultValue = "#{T(java.time.LocalDateTime).now()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateTime
    ) {
        return this.leaderboardService.getLeaderboard(game, stat, board, startPosition, endPosition, filter, dateTime);
    }
}
