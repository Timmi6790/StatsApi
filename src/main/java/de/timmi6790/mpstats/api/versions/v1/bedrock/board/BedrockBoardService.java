package de.timmi6790.mpstats.api.versions.v1.bedrock.board;

import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Service;

@Service
public class BedrockBoardService extends BoardService {
    public BedrockBoardService(final Jdbi jdbi) {
        super(jdbi, "bedrock");
    }
}
