package de.timmi6790.mpstats.api.versions.v1.bedrock.game;

import de.timmi6790.mpstats.api.AbstractIntegrationTest;
import de.timmi6790.mpstats.api.versions.v1.common.game.AbstractGameServiceTest;

class BedrockGameServiceTest extends AbstractGameServiceTest {
    public BedrockGameServiceTest() {
        super(() -> new BedrockGameService(AbstractIntegrationTest.jdbi()));
    }
}