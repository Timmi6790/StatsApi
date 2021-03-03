package de.timmi6790.mpstats.api.versions.v1.java.groups.repository.mysql.mappers;

import de.timmi6790.mpstats.api.versions.v1.java.groups.models.GroupsModel;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class GroupsMapper implements RowMapper<GroupsModel> {
    @Override
    public GroupsModel map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        final String description = rs.getString("description");
        final String aliasNames = rs.getString("aliasNames");
        final String gameNames = rs.getString("gameNames");

        return new GroupsModel(
                rs.getString("groupName"),
                description == null ? "" : description,
                aliasNames == null ? new ArrayList<>() : Arrays.asList(aliasNames.split(",")),
                gameNames == null ? new ArrayList<>() : Arrays.asList(gameNames.split(","))
        );
    }
}
