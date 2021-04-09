package de.timmi6790.mpstats.api.versions.v1.java.groups.repository;

import de.timmi6790.mpstats.api.versions.v1.java.groups.repository.models.Group;

import java.util.List;
import java.util.Optional;

public interface JavaGroupRepository {
    List<Group> getGroups();

    Optional<Group> getGroup(String groupName);

    Group createGroup(String groupName, String cleanName);

    void deleteGroup(int groupId);

    void renameGroup(int groupId, String newGroupName);

    void setDescription(int groupId, String newDescription);

    void addGame(int groupId, int gameId);

    void removeGame(int groupId, int gameId);
}
