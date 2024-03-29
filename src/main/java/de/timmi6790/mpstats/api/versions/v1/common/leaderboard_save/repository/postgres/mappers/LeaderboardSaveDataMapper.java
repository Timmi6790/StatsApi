package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.repository.postgres.mappers;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.repository.postgres.models.LeaderboardSaveData;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;

public class LeaderboardSaveDataMapper implements RowMapper<LeaderboardSaveData> {
    @Override
    public LeaderboardSaveData map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        return new LeaderboardSaveData(
                rs.getInt("leaderboard_id"),
                rs.getInt("saveId"),
                rs.getTimestamp("save_time").toLocalDateTime().atZone(ZoneId.systemDefault())
        );
    }
}
