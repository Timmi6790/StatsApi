package de.timmi6790.mpstats.api.versions.v1.common.group.repository.postgres;

import de.timmi6790.mpstats.api.versions.v1.common.group.repository.GroupRepository;
import de.timmi6790.mpstats.api.versions.v1.common.group.repository.models.Group;
import de.timmi6790.mpstats.api.versions.v1.common.group.repository.postgres.mappers.GroupMapper;
import de.timmi6790.mpstats.api.versions.v1.common.group.repository.postgres.reducers.GroupReducer;
import de.timmi6790.mpstats.api.versions.v1.common.utilities.PostgresRepository;
import org.jdbi.v3.core.Jdbi;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GroupPostgresRepository extends PostgresRepository implements GroupRepository {
    private final String insertGroup;
    private final String deleteGroup;

    private final String getGroups;
    private final String getGroup;

    private final Jdbi database;

    public GroupPostgresRepository(final Jdbi jdbi, final String schema) {
        super(jdbi, schema);
        this.database = jdbi;

        this.database.registerRowMapper(new GroupMapper());

        // Create queries
        this.insertGroup = this.formatQuery(QueryTemplates.INSERT_GROUP);
        this.deleteGroup = this.formatQuery(QueryTemplates.DELETE_GROUP);

        this.getGroups = this.formatQuery(QueryTemplates.GET_GROUPS);
        this.getGroup = this.formatQuery(QueryTemplates.GET_GROUP);
    }

    @Override
    public List<Group> getGroups() {
        return this.database.withHandle(handle ->
                handle.createQuery(this.getGroups)
                        .reduceRows(new GroupReducer())
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Optional<Group> getGroup(final String groupName) {
        return this.database.withHandle(handle ->
                handle.createQuery(this.getGroup)
                        .bind("groupName", groupName)
                        .reduceRows(new GroupReducer())
                        .findFirst()
        );
    }

    @Override
    public Group createGroup(final String groupName, final String cleanName) {
        return this.database.withHandle(handle ->
                handle.createQuery(this.insertGroup)
                        .bind("groupName", groupName)
                        .bind("cleanName", cleanName)
                        .mapTo(Group.class)
                        .first()
        );
    }

    @Override
    public void deleteGroup(final int groupId) {
        this.database.useHandle(handle ->
                handle.createUpdate(this.deleteGroup)
                        .bind("groupId", groupId)
                        .execute()
        );
    }

    @Override
    public void renameGroup(final int groupId, final String newGroupName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDescription(final int groupId, final String newDescription) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addAliasName(final int groupId, final String aliasName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAliasName(final int groupId, final String aliasName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addGame(final int groupId, final int gameId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeGame(final int groupId, final int gameId) {
        throw new UnsupportedOperationException();
    }

    private static class QueryTemplates {
        private static final String INSERT_GROUP = "INSERT INTO $schema$.groups(group_name, clean_name) VALUES(:groupName, :cleanName) RETURNING id group_id, group_name, clean_name, group_description;";
        private static final String DELETE_GROUP = "DELETE FROM $schema$.groups WHERE id = :groupId;";

        private static final String GET_GROUPS_BASE = "SELECT id group_id, group_name, clean_name, group_description " +
                "FROM $schema$.groups " +
                "%s;";

        private static final String GET_GROUPS = String.format(GET_GROUPS_BASE, "");
        private static final String GET_GROUP = String.format(GET_GROUPS_BASE, "WHERE LOWER(group_name) = LOWER(:groupName)");
    }
}
