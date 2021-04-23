package de.timmi6790.mpstats.api.versions.v1.bedrock.board;

import de.timmi6790.mpstats.api.utilities.bedrock.BedrockServiceGenerator;
import de.timmi6790.mpstats.api.versions.v1.common.board.AbstractBoardServiceTest;

class BedrockBoardServiceTest extends AbstractBoardServiceTest {
    public BedrockBoardServiceTest() {
        super(BedrockServiceGenerator::generateBoardService);
    }
}