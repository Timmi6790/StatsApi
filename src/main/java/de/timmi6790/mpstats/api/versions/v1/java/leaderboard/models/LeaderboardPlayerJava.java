package de.timmi6790.mpstats.api.versions.v1.java.leaderboard.models;

import de.timmi6790.commons.utilities.UUIDUtilities;
import lombok.Data;
import lombok.NonNull;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Data
public class LeaderboardPlayerJava {
    @NonNull
    private final UUID uuid;
    @NonNull
    private final String name;

    public static class DatabaseMapper implements RowMapper<LeaderboardPlayerJava> {
        @Override
        public LeaderboardPlayerJava map(final ResultSet rs, final StatementContext ctx) throws SQLException {
            return new LeaderboardPlayerJava(
                    UUIDUtilities.getUUIDFromBytes(rs.getBytes("uuid")),
                    rs.getString("player")
            );
        }
    }
}
