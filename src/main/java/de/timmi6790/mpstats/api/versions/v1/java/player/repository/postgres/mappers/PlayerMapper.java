package de.timmi6790.mpstats.api.versions.v1.java.player.repository.postgres.mappers;

import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.Player;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerMapper implements RowMapper<Player> {
    @Override
    public Player map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        return new Player(
                rs.getInt("player_id"),
                rs.getString("player_name"),
                UUID.fromString(rs.getString("player_uuid"))
        );
    }
}
