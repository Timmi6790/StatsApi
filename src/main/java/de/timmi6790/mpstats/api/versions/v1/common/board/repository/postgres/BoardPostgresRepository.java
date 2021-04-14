package de.timmi6790.mpstats.api.versions.v1.common.board.repository.postgres;

import de.timmi6790.mpstats.api.versions.v1.common.board.repository.BoardRepository;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.postgres.mappers.BoardMapper;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.postgres.reducers.BoardReducer;
import de.timmi6790.mpstats.api.versions.v1.common.utilities.PostgresRepository;
import org.jdbi.v3.core.Jdbi;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BoardPostgresRepository extends PostgresRepository implements BoardRepository {
    private final String getBoards;
    private final String getBoard;

    private final String insertBoard;
    private final String removeBoard;

    public BoardPostgresRepository(final Jdbi database, final String schema) {
        super(database, schema);

        this.getDatabase()
                .registerRowMapper(new BoardMapper());

        // Create queries
        this.getBoards = this.formatQuery(QueryTemplates.GET_BOARDS);
        this.getBoard = this.formatQuery(QueryTemplates.GET_BOARD);
        this.insertBoard = this.formatQuery(QueryTemplates.INSERT_BOARD);
        this.removeBoard = this.formatQuery(QueryTemplates.REMOVE_BOARD);
    }

    @Override
    public List<Board> getBoards() {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getBoards)
                        .reduceRows(new BoardReducer())
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Optional<Board> getBoard(final String boardName) {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getBoard)
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
        this.getDatabase().useHandle(handle ->
                handle.createUpdate(this.insertBoard)
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
        this.getDatabase().useHandle(handle ->
                handle.createUpdate(this.removeBoard)
                        .bind("boardId", boardId)
                        .execute()
        );
    }

    @Override
    public void setBoardName(final int boardId, final String newBoardName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBoardWebsiteName(final int boardId, final String newWebsiteName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBoardUpdateTime(final int boardId, final long newUpdateTime) {
        throw new UnsupportedOperationException();
    }

    private static class QueryTemplates {
        private static final String GET_BOARD_BASE = "SELECT board.\"id\" board_id, board.website_name website_name, board.board_name board_name, board.clean_name clean_name, board.update_time update_time, alias_name.alias_name alias_name " +
                "FROM $schema$.boards board " +
                "LEFT JOIN $schema$.board_alias alias_name ON alias_name.board_id = board.\"id\" " +
                "%s;";

        private static final String GET_BOARDS = String.format(GET_BOARD_BASE, "");
        private static final String GET_BOARD = String.format(GET_BOARD_BASE, "WHERE LOWER(board.board_name) = LOWER(:boardName)");

        private static final String INSERT_BOARD = "INSERT INTO $schema$.boards(website_name, board_name, clean_name, update_time) VALUES(:websiteName, :boardName, :cleanName, :updateTime);";
        private static final String REMOVE_BOARD = "DELETE FROM $schema$.boards WHERE id = :boardId;";
    }
}
