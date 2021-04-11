package de.timmi6790.mpstats.api.versions.v1.java.board;

import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JavaBoardService extends BoardService {
    @Autowired
    public JavaBoardService(final Jdbi jdbi) {
        super(jdbi, "java");
    }
}
