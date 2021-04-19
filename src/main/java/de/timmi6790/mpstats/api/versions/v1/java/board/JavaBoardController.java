package de.timmi6790.mpstats.api.versions.v1.java.board;

import de.timmi6790.mpstats.api.versions.v1.common.board.BoardController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/java/board")
@Tag(name = "Java - Board")
public class JavaBoardController extends BoardController {
    @Autowired
    public JavaBoardController(final JavaBoardService boardService) {
        super(boardService);
    }
}
