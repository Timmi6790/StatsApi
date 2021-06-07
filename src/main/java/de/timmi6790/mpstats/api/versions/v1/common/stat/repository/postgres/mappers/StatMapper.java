package de.timmi6790.mpstats.api.versions.v1.common.stat.repository.postgres.mappers;

import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.StatType;
import lombok.extern.log4j.Log4j2;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

@Log4j2
public class StatMapper implements RowMapper<Stat> {
    @Override
    public Stat map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        final String typeString = rs.getString("type_name");
        StatType type;
        try {
            type = StatType.valueOf(typeString);
        } catch (final IllegalArgumentException e) {
            log.warn("Illegal stat type found of " + typeString);
            type = StatType.getDefault();
        }

        return new Stat(
                rs.getInt("stat_id"),
                rs.getString("website_name"),
                rs.getString("stat_name"),
                rs.getString("clean_name"),
                rs.getString("description"),
                rs.getBoolean("achievement"),
                new HashSet<>(),
                rs.getInt("sorting_priority"),
                type
        );
    }
}
