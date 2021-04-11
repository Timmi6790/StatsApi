package de.timmi6790.mpstats.api.versions.v1.common.stat.repository.postgres.mappers;

import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

public class StatMapper implements RowMapper<Stat> {
    @Override
    public Stat map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        return new Stat(
                rs.getInt("stat_id"),
                rs.getString("website_name"),
                rs.getString("stat_name"),
                rs.getString("clean_name"),
                rs.getString("description"),
                rs.getBoolean("achievement"),
                new HashSet<>()
        );
    }
}
