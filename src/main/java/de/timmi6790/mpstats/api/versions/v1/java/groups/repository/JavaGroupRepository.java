package de.timmi6790.mpstats.api.versions.v1.java.groups.repository;

import de.timmi6790.mpstats.api.versions.v1.java.groups.models.GroupsModel;

import java.util.List;

public interface JavaGroupRepository {
    List<GroupsModel> getGroups();
}
