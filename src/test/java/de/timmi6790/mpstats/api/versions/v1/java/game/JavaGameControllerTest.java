package de.timmi6790.mpstats.api.versions.v1.java.game;

import de.timmi6790.mpstats.api.versions.v1.common.game.AbstractGameControllerTest;
import org.springframework.beans.factory.annotation.Autowired;

class JavaGameControllerTest extends AbstractGameControllerTest<JavaGameService> {
    @Autowired
    private JavaGameService gameService;
    
    public JavaGameControllerTest() {
        super("/v1/java/game");
    }

    @Override
    protected JavaGameService getGameService() {
        return this.gameService;
    }
}