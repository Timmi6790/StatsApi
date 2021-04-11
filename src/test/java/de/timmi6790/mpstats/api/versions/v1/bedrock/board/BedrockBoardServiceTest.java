package de.timmi6790.mpstats.api.versions.v1.bedrock.board;

import de.timmi6790.mpstats.api.AbstractIntegrationTest;
import de.timmi6790.mpstats.api.versions.v1.common.board.AbstractBoardServiceTest;

class BedrockBoardServiceTest extends AbstractBoardServiceTest {
    public BedrockBoardServiceTest() {
        super(() -> new BedrockBoardService(AbstractIntegrationTest.jdbi()));
    }
}