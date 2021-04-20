package de.timmi6790.mpstats.api.utilities;

import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class BoardUtilities {
    private static final AtomicInteger BOARD_ID = new AtomicInteger(0);

    public static String generateBoardName() {
        return "Board" + BOARD_ID.incrementAndGet();
    }

    public static Board generateBoard(final BoardService boardService) {
        final String boardName = generateBoardName();
        return generateBoard(boardService, boardName);
    }

    public static Board generateBoard(final BoardService boardService, final String boardName) {
        final String websiteName = generateBoardName();
        final String cleanName = generateBoardName();
        final int updateTime = ThreadLocalRandom.current().nextInt(5_000);

        return boardService.getBoardOrCreate(websiteName, boardName, cleanName, updateTime);
    }
}
