package de.timmi6790.mpstats.api.versions.v1.bedrock.game;

import de.timmi6790.mpstats.api.versions.v1.common.game.AbstractGameControllerTest;
import org.springframework.beans.factory.annotation.Autowired;

class BedrockGameControllerTest extends AbstractGameControllerTest<BedrockGameService> {
    @Autowired
    private BedrockGameService gameService;

    public BedrockGameControllerTest() {
        super("/v1/bedrock/game");
    }

    @Override
    protected BedrockGameService getGameService() {
        return this.gameService;
    }
}