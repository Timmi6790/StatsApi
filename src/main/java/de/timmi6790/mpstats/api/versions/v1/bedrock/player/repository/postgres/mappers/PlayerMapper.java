package de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.postgres.mappers;

import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockRepositoryPlayer;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerMapper implements RowMapper<BedrockRepositoryPlayer> {
    @Override
    public BedrockRepositoryPlayer map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        return new BedrockRepositoryPlayer(
                rs.getString("player_name"),
                rs.getInt("player_id")
        );
    }
}
