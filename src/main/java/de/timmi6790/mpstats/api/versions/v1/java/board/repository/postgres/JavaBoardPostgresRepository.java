package de.timmi6790.mpstats.api.versions.v1.java.board.repository.postgres;

import de.timmi6790.mpstats.api.versions.v1.java.board.repository.JavaBoardRepository;
import de.timmi6790.mpstats.api.versions.v1.java.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.java.board.repository.postgres.mappers.BoardMapper;
import de.timmi6790.mpstats.api.versions.v1.java.board.repository.postgres.reducers.BoardReducer;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JavaBoardPostgresRepository implements JavaBoardRepository {
    private static final String GET_BOARD_BASE = "SELECT board.\"id\" board_id, board.website_name website_name, board.board_name board_name, board.clean_name clean_name, board.update_time update_time, alias_name.alias_name alias_name " +
            "FROM java.boards board " +
            "LEFT JOIN java.board_alias alias_name ON alias_name.board_id = board.\"id\" " +
            "%s;";

    private static final String GET_BOARDS = String.format(GET_BOARD_BASE, "");
    private static final String GET_BOARD = String.format(GET_BOARD_BASE, "WHERE LOWER(board.board_name) = LOWER(:boardName)");

    private static final String INSERT_BOARD = "INSERT INTO java.boards(website_name, board_name, clean_name, update_time) VALUES(:websiteName, :boardName, :cleanName, :updateTime);";
    private static final String REMOVE_BOARD = "DELETE FROM java.boards WHERE id = :boardId;";


    private final Jdbi database;

    @Autowired
    public JavaBoardPostgresRepository(final Jdbi database) {
        this.database = database;

        database.registerRowMapper(new BoardMapper());
    }

    @Override
    public List<Board> getBoards() {
        return this.database.withHandle(handle ->
                handle.createQuery(GET_BOARDS)
                        .reduceRows(new BoardReducer())
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Optional<Board> getBoard(final String boardName) {
        return this.database.withHandle(handle ->
                handle.createQuery(GET_BOARD)
                        .bind("boardName", boardName)
                        .reduceRows(new BoardReducer())
                        .findFirst()
        );
    }

    @Override
    public Board createBoard(final String boardName,
                             final String websiteName,
                             final String cleanName,
                             final int updateTime) {
        this.database.useHandle(handle ->
                handle.createUpdate(INSERT_BOARD)
                        .bind("websiteName", websiteName)
                        .bind("boardName", boardName)
                        .bind("cleanName", cleanName)
                        .bind("updateTime", updateTime)
                        .execute()
        );

        return this.getBoard(boardName).orElseThrow(RuntimeException::new);
    }

    @Override
    public void removeBoard(final int boardId) {
        this.database.useHandle(handle ->
                handle.createUpdate(REMOVE_BOARD)
                        .bind("boardId", boardId)
                        .execute()
        );
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
