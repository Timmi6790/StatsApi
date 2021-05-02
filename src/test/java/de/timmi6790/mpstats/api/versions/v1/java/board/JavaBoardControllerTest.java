package de.timmi6790.mpstats.api.versions.v1.java.board;

import de.timmi6790.mpstats.api.versions.v1.common.board.AbstractBoardControllerTest;
import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import org.springframework.beans.factory.annotation.Autowired;

class JavaBoardControllerTest extends AbstractBoardControllerTest<BoardService> {
    @Autowired
    private JavaBoardService boardService;

    public JavaBoardControllerTest() {
        super("/v1/java/board");
    }

    @Override
    protected BoardService getBoardService() {
        return this.boardService;
    }
}