package de.timmi6790.mpstats.api.versions.v1.java.board.repository;

import de.timmi6790.mpstats.api.versions.v1.java.board.repository.models.Board;

import java.util.List;
import java.util.Optional;

public interface JavaBoardRepository {
    List<Board> getBoards();

    Optional<Board> getBoard(String boardName);

    Board createBoard(String boardName, String websiteName, String cleanName, int updateTime);

    void removeBoard(int boardId);

    void setBoardName(int boardId, String newBoardName);

    void setBoardWebsiteName(int boardId, String newWebsiteName);

    void setBoardUpdateTime(int boardId, long newUpdateTime);
}
