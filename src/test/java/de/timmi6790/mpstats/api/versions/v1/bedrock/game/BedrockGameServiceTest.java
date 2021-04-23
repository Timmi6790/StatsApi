package de.timmi6790.mpstats.api.versions.v1.bedrock.game;

import de.timmi6790.mpstats.api.utilities.bedrock.BedrockServiceGenerator;
import de.timmi6790.mpstats.api.versions.v1.common.game.AbstractGameServiceTest;

class BedrockGameServiceTest extends AbstractGameServiceTest {
    public BedrockGameServiceTest() {
        super(BedrockServiceGenerator::generateGameService);
    }
}