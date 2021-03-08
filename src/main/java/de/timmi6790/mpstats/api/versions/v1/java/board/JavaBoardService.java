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
    }

    public boolean hasBoard(final String boardName) {
        return this.boardNames.contains(boardName);
    }

    public List<Board> getBoards() {
        return new ArrayList<>();
    }

    public Optional<Board> getBoard(final String boardName) {
        return Optional.empty();
    }

    public Board createBoard(final String boardName, final String websiteName, final int updateTime) {
        return null;
    }

    public void deleteBoard(final String gameName) {

    }
}
