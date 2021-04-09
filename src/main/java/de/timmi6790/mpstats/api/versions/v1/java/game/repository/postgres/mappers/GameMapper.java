package de.timmi6790.mpstats.api.versions.v1.java.game.repository.postgres.mappers;

import de.timmi6790.mpstats.api.versions.v1.java.game.repository.models.Game;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

public class GameMapper implements RowMapper<Game> {
    @Override
    public Game map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        return new Game(
                rs.getInt("game_id"),
                rs.getString("website_name"),
                rs.getString("game_name"),
                rs.getString("clean_name"),
                new HashSet<>(),
                rs.getString("category_name"),
                rs.getString("description"),
                rs.getString("wiki_url")
        );
    }
}
