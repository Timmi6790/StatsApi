package de.timmi6790.mpstats.api.versions.v1.common.group.repository;


import de.timmi6790.mpstats.api.versions.v1.common.group.repository.models.Group;

import java.util.List;
import java.util.Optional;

public interface GroupRepository {
    List<Group> getGroups();

    Optional<Group> getGroup(String groupName);

    Group createGroup(String groupName, String cleanName);

    void deleteGroup(int groupId);

    void renameGroup(int groupId, String newGroupName);

    void setDescription(int groupId, String newDescription);

    void addAliasName(int groupId, String aliasName);

    void removeAliasName(int groupId, String aliasName);

    void addGame(int groupId, int gameId);

    void removeGame(int groupId, int gameId);
}
