package de.timmi6790.mpstats.api.versions.v1.common.board.repository.postgres.mappers;

import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

public class BoardMapper implements RowMapper<Board> {
    @Override
    public Board map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        return new Board(
                rs.getInt("board_id"),
                rs.getString("website_name"),
                rs.getString("board_name"),
                rs.getString("clean_name"),
                rs.getInt("update_time"),
                new HashSet<>()
        );
    }
}
