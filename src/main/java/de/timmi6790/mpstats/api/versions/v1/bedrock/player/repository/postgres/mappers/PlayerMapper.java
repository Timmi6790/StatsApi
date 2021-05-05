package de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.postgres.mappers;

import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockPlayer;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerMapper implements RowMapper<BedrockPlayer> {
    @Override
    public BedrockPlayer map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        return new BedrockPlayer(
                rs.getInt("player_id"),
                rs.getString("player_name")
        );
    }
}
