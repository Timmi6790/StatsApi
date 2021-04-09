package de.timmi6790.mpstats.api.versions.v1.java.board;

import com.google.common.util.concurrent.Striped;
import de.timmi6790.mpstats.api.versions.v1.java.board.repository.JavaBoardRepository;
import de.timmi6790.mpstats.api.versions.v1.java.board.repository.models.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

@Service
public class JavaBoardService {
    private final JavaBoardRepository javaBoardRepository;

    private final Striped<Lock> boardLock = Striped.lock(32);
    private final Map<String, Board> boards;

    @Autowired
    public JavaBoardService(final JavaBoardRepository javaBoardRepository) {
        this.javaBoardRepository = javaBoardRepository;

        // Load existing boards from repository
        final List<Board> existingBoards = this.getBoards();
        this.boards = new LinkedCaseInsensitiveMap<>(existingBoards.size());
        for (final Board board : existingBoards) {
            this.boards.put(board.getBoardName(), board);
        }
    }

    private Lock getBoardLock(final String boardName) {
        return this.boardLock.get(boardName.toLowerCase());
    }

    public boolean hasBoard(final String boardName) {
        return this.boards.containsKey(boardName);
    }

    public List<Board> getBoards() {
        return this.javaBoardRepository.getBoards();
    }

    public Optional<Board> getBoard(final String boardName) {
        return Optional.ofNullable(this.boards.get(boardName));
    }

    public Board getOrCreateBoard(final String websiteName,
                                  final String boardName,
                                  final String cleanName,
                                  final int updateTime) {
        final Lock lock = this.getBoardLock(boardName);
        lock.lock();
        try {
            if (this.hasBoard(boardName)) {
                return this.getBoard(boardName).orElseThrow(RuntimeException::new);
            }

            final Board board = this.javaBoardRepository.createBoard(boardName, websiteName, cleanName, updateTime);
            this.boards.put(board.getBoardName(), board);
            return board;
        } finally {
            lock.unlock();
        }
    }

    public void deleteBoard(final String boardName) {
        final Lock lock = this.getBoardLock(boardName);
        lock.lock();
        try {
            final Optional<Board> boardOpt = this.getBoard(boardName);
            if (boardOpt.isPresent()) {
                this.boards.remove(boardOpt.get().getBoardName());
                this.javaBoardRepository.removeBoard(boardOpt.get().getRepositoryId());
            }
        } finally {
            lock.unlock();
        }
    }
}
