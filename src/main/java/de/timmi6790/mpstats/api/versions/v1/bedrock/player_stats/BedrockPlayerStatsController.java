package de.timmi6790.mpstats.api.versions.v1.bedrock.player_stats;

import de.timmi6790.mpstats.api.versions.v1.bedrock.board.BedrockBoardService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.game.BedrockGameService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.BedrockPlayerService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockPlayer;
import de.timmi6790.mpstats.api.versions.v1.bedrock.stat.BedrockStatService;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.PlayerStatsController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/bedrock/player")
@Tag(name = "Bedrock - Player")
public class BedrockPlayerStatsController extends PlayerStatsController<BedrockPlayer, BedrockPlayerService> {
    @Autowired
    public BedrockPlayerStatsController(final BedrockPlayerStatsService playerStatsService,
                                        final BedrockGameService gameService,
                                        final BedrockStatService statService,
                                        final BedrockBoardService boardService,
                                        final BedrockPlayerService playerService) {
        super(playerStatsService, gameService, statService, boardService, playerService);
    }
}
