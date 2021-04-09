package de.timmi6790.mpstats.api.versions.v1.java.board;

import de.timmi6790.mpstats.api.versions.v1.java.board.repository.JavaBoardRepository;
import de.timmi6790.mpstats.api.versions.v1.java.board.repository.models.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JavaBoardService {
    private final JavaBoardRepository javaBoardRepository;

    private final Set<String> boardNames = Collections.synchronizedSet(new TreeSet<>(String.CASE_INSENSITIVE_ORDER));

    @Autowired
    public JavaBoardService(final JavaBoardRepository javaBoardRepository) {
        this.javaBoardRepository = javaBoardRepository;

        for (final Board board : this.getBoards()) {
            this.boardNames.add(board.getBoardName());
        }
    }

    public boolean hasBoard(final String boardName) {
        return this.boardNames.contains(boardName);
    }

    public List<Board> getBoards() {
        return this.javaBoardRepository.getBoards();
    }

    public Optional<Board> getBoard(final String boardName) {
        if (this.hasBoard(boardName)) {
            return this.javaBoardRepository.getBoard(boardName);
        }
        return Optional.empty();
    }

    public Board getOrCreateBoard(final String websiteName,
                                  final String boardName,
                                  final String cleanName,
                                  final int updateTime) {
        if (this.hasBoard(boardName)) {
            return this.getBoard(boardName).orElseThrow(RuntimeException::new);
        }

        final Board board = this.javaBoardRepository.createBoard(boardName, websiteName, cleanName, updateTime);
        this.boardNames.add(board.getBoardName());
        return board;
    }

    public void deleteBoard(final String boardName) {
        final Optional<Board> boardOpt = this.getBoard(boardName);
        if (boardOpt.isPresent()) {
            this.boardNames.remove(boardName);
            this.javaBoardRepository.removeBoard(boardOpt.get().getRepositoryId());
        }
    }
}
