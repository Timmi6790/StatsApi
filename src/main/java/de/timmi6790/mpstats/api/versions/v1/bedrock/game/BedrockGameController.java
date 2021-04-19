package de.timmi6790.mpstats.api.versions.v1.bedrock.game;

import de.timmi6790.mpstats.api.versions.v1.common.game.GameController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/bedrock/game")
@Tag(name = "Bedrock - Game")
public class BedrockGameController extends GameController {
    @Autowired
    public BedrockGameController(final BedrockGameService gameService) {
        super(gameService);
    }
}
