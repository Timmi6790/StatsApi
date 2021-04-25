package de.timmi6790.mpstats.api.versions.v1.common.board;

import com.google.common.util.concurrent.Striped;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.BoardRepository;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.postgres.BoardPostgresRepository;
import lombok.AccessLevel;
import lombok.Getter;
import org.jdbi.v3.core.Jdbi;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

public class BoardService {
    @Getter(AccessLevel.PROTECTED)
    private final BoardRepository boardRepository;

    private final Striped<Lock> boardLock = Striped.lock(32);
    private final Map<String, Board> boards;

    public BoardService(final Jdbi jdbi, final String schema) {
        this.boardRepository = new BoardPostgresRepository(jdbi, schema);

        // Load existing boards from repository
        final List<Board> existingBoards = this.boardRepository.getBoards();
        this.boards = new LinkedCaseInsensitiveMap<>(existingBoards.size());
        for (final Board board : existingBoards) {
            this.boards.put(board.boardName(), board);
        }
    }

    private Lock getBoardLock(final String boardName) {
        return this.boardLock.get(boardName.toLowerCase());
    }

    public boolean hasBoard(final String boardName) {
        return this.boards.containsKey(boardName);
    }

    public List<Board> getBoards() {
        return new ArrayList<>(this.boards.values());
    }

    public Optional<Board> getBoard(final String boardName) {
        return Optional.ofNullable(this.boards.get(boardName));
    }

    public Board getBoardOrCreate(final String websiteName,
                                  final String boardName,
                                  final String cleanName,
                                  final int updateTime) {
        final Lock lock = this.getBoardLock(boardName);
        lock.lock();
        try {
            if (this.hasBoard(boardName)) {
                return this.getBoard(boardName).orElseThrow(RuntimeException::new);
            }

            final Board board = this.boardRepository.createBoard(boardName, websiteName, cleanName, updateTime);
            this.boards.put(board.boardName(), board);
            return board;
        } finally {
            lock.unlock();
        }
    }

    public void deleteBoard(final String boardName) {
        final Lock lock = this.getBoardLock(boardName);
        lock.lock();
        try {
            final Board board = this.boards.remove(boardName);
            if (board != null) {
                this.boardRepository.removeBoard(board.repositoryId());
            }
        } finally {
            lock.unlock();
        }
    }
}
