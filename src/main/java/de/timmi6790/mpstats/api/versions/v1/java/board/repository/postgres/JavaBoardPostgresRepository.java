package de.timmi6790.mpstats.api.versions.v1.java.board.repository.postgres;

import de.timmi6790.mpstats.api.versions.v1.java.board.repository.JavaBoardRepository;
import de.timmi6790.mpstats.api.versions.v1.java.board.repository.models.Board;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JavaBoardPostgresRepository implements JavaBoardRepository {
    @Override
    public List<Board> getBoards() {
        return null;
    }

    @Override
    public Optional<Board> getBoard(final String boardName, final String websiteName, final long updateTime) {
        return Optional.empty();
    }

    @Override
    public Board createBoard(final String boardName, final String websiteName, final int updateTime) {
        return null;
    }

    @Override
    public void removeBoard(final int boardId) {

    }

    @Override
    public void setBoardName(final int boardId, final String newBoardName) {

    }

    @Override
    public void setBoardWebsiteName(final int boardId, final String newWebsiteName) {

    }

    @Override
    public void setBoardUpdateTime(final int boardId, final long newUpdateTime) {

    }
}
