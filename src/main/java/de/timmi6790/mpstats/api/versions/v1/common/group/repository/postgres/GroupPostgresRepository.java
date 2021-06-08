package de.timmi6790.mpstats.api.versions.v1.common.group.repository.postgres;

import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
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

    private final GameService gameService;

    public GroupPostgresRepository(final Jdbi jdbi, final String schema, final GameService gameService) {
        super(jdbi, schema);

        this.gameService = gameService;

        this.getDatabase().registerRowMapper(new GroupMapper());

        // Create queries
        this.insertGroup = this.formatQuery(QueryTemplates.INSERT_GROUP);
        this.deleteGroup = this.formatQuery(QueryTemplates.DELETE_GROUP);

        this.getGroups = this.formatQuery(QueryTemplates.GET_GROUPS);
        this.getGroup = this.formatQuery(QueryTemplates.GET_GROUP);
    }

    protected GroupReducer constructGroupReducer() {
        return new GroupReducer(this.gameService);
    }

    @Override
    public List<Group> getGroups() {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getGroups)
                        .reduceRows(this.constructGroupReducer())
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Optional<Group> getGroup(final String groupName) {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getGroup)
                        .bind("groupName", groupName)
                        .reduceRows(this.constructGroupReducer())
                        .findFirst()
        );
    }

    @Override
    public Group createGroup(final String groupName, final String cleanName) {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.insertGroup)
                        .bind("groupName", groupName)
                        .bind("cleanName", cleanName)
                        .mapTo(Group.class)
                        .first()
        );
    }

    @Override
    public void deleteGroup(final int groupId) {
        this.getDatabase().useHandle(handle ->
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

        private static final String GET_GROUPS_BASE = "SELECT g.id group_id, group_name, clean_name, group_description, name.alias_name alias_name, game.game_id game_id " +
                "FROM $schema$.groups g " +
                "LEFT JOIN $schema$.group_alias_names name ON name.group_id = g.id " +
                "LEFT JOIN $schema$.group_games game ON game.group_id = g.id " +
                "%s;";

        private static final String GET_GROUPS = String.format(GET_GROUPS_BASE, "");
        private static final String GET_GROUP = String.format(GET_GROUPS_BASE, "WHERE LOWER(group_name) = LOWER(:groupName)");
    }
}
