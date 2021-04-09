package de.timmi6790.mpstats.api.versions.v1.java.game.repository.postgres.mappers;

import de.timmi6790.mpstats.api.versions.v1.java.game.repository.models.GameCategory;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GameCategoryMapper implements RowMapper<GameCategory> {
    @Override
    public GameCategory map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        return new GameCategory(
                rs.getInt("id"),
                rs.getString("category_name")
        );
    }
}
