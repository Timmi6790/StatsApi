package de.timmi6790.mpstats.api.versions.v1.java.groups.repository.postgres;

import de.timmi6790.mpstats.api.versions.v1.java.groups.repository.JavaGroupRepository;
import de.timmi6790.mpstats.api.versions.v1.java.groups.repository.models.Group;
import de.timmi6790.mpstats.api.versions.v1.java.groups.repository.postgres.mappers.GroupMapper;
import de.timmi6790.mpstats.api.versions.v1.java.groups.repository.postgres.reducers.GroupReducer;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JavaGroupPostgresRepository implements JavaGroupRepository {
    private static final String INSERT_GROUP = "INSERT INTO java_group.groups(group_name) VALUES(:groupName) RETURNING id group_id, group_name, group_description;";
    private static final String DELETE_GROUP = "DELETE FROM java_group.groups WHERE id = :groupId;";

    private static final String GET_GROUPS_BASE = "SELECT id group_id, group_name, group_description " +
            "FROM java_group.groups " +
            "%s;";

    private static final String GET_GROUPS = String.format(GET_GROUPS_BASE, "");
    private static final String GET_GROUP = String.format(GET_GROUPS_BASE, "WHERE group_name = :groupName");

    private final Jdbi database;

    @Autowired
    public JavaGroupPostgresRepository(final Jdbi jdbi) {
        this.database = jdbi;

        this.database.registerRowMapper(new GroupMapper());
    }

    @Override
    public List<Group> getGroups() {
        return this.database.withHandle(handle ->
                handle.createQuery(GET_GROUPS)
                        .reduceRows(new GroupReducer())
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Optional<Group> getGroup(final String groupName) {
        return this.database.withHandle(handle ->
                handle.createQuery(GET_GROUP)
                        .bind("groupName", groupName)
                        .reduceRows(new GroupReducer())
                        .findFirst()
        );
    }

    @Override
    public Group createGroup(final String groupName) {
        return this.database.withHandle(handle ->
                handle.createQuery(INSERT_GROUP)
                        .bind("groupName", groupName)
                        .mapTo(Group.class)
                        .first()
        );
    }

    @Override
    public void deleteGroup(final int groupId) {
        this.database.useHandle(handle ->
                handle.createUpdate(DELETE_GROUP)
                        .bind("groupId", groupId)
                        .execute()
        );
    }

    @Override
    public void renameGroup(final int groupId, final String newGroupName) {

    }

    @Override
    public void setDescription(final int groupId, final String newDescription) {

    }

    @Override
    public void addGame(final int groupId, final int gameId) {

    }

    @Override
    public void removeGame(final int groupId, final int gameId) {

    }
}
