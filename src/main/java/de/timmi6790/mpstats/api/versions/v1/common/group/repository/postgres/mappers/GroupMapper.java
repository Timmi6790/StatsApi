package de.timmi6790.mpstats.api.versions.v1.common.group.repository.postgres.mappers;

import de.timmi6790.mpstats.api.versions.v1.common.group.repository.models.Group;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.WeakHashMap;

public class GroupMapper implements RowMapper<Group> {
    @Override
    public Group map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        final String description = rs.getString("group_description");

        return new Group(
                rs.getInt("group_id"),
                rs.getString("group_name"),
                rs.getString("clean_name"),
                description == null ? "" : description,
                new HashSet<>(),
                Collections.newSetFromMap(new WeakHashMap<>())
        );
    }
}
