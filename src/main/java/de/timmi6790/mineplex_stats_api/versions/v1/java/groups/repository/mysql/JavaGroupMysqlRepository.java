package de.timmi6790.mineplex_stats_api.versions.v1.java.groups.repository.mysql;

import de.timmi6790.mineplex_stats_api.versions.v1.java.groups.models.GroupsModel;
import de.timmi6790.mineplex_stats_api.versions.v1.java.groups.repository.JavaGroupRepository;
import de.timmi6790.mineplex_stats_api.versions.v1.java.groups.repository.mysql.mappers.GroupsMapper;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JavaGroupMysqlRepository implements JavaGroupRepository {
    private static final String GET_GROUPS = "SELECT jgroup.group_name groupName, jgroup.description, GROUP_CONCAT(DISTINCT groupAlias.alias_name) aliasNames, GROUP_CONCAT(DISTINCT game.game_name) gameNames " +
            "FROM java_group jgroup " +
            "LEFT JOIN java_group_alias groupAlias ON groupAlias.group_id = jgroup.id " +
            "INNER JOIN java_group_game groupGame ON groupGame.group_id = jgroup.id " +
            "INNER JOIN java_game game ON game.id = groupGame.game_id " +
            "GROUP BY jgroup.id; ";


    private final Jdbi database;

    @Autowired
    public JavaGroupMysqlRepository(final Jdbi jdbi) {
        this.database = jdbi;

        this.database.registerRowMapper(new GroupsMapper());
    }

    @Override
    public List<GroupsModel> getGroups() {
        return this.database.withHandle(handle ->
                handle.createQuery(GET_GROUPS)
                        .mapTo(GroupsModel.class)
                        .list()
        );
    }
}
