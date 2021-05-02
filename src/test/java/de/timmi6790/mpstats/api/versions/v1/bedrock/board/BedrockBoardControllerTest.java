package de.timmi6790.mpstats.api.versions.v1.bedrock.board;

import de.timmi6790.mpstats.api.versions.v1.common.board.AbstractBoardControllerTest;
import org.springframework.beans.factory.annotation.Autowired;

class BedrockBoardControllerTest extends AbstractBoardControllerTest<BedrockBoardService> {
    @Autowired
    private BedrockBoardService boardService;

    public BedrockBoardControllerTest() {
        super("/v1/bedrock/board");
    }

    @Override
    protected BedrockBoardService getBoardService() {
        return this.boardService;
    }
}