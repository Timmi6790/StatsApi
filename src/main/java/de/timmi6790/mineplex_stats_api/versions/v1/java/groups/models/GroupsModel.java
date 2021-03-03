package de.timmi6790.mineplex_stats_api.versions.v1.java.groups.models;

import lombok.Data;

import java.util.List;

@Data
public class GroupsModel {
    private final String groupName;
    private final String description;
    private final List<String> aliasNames;
    private final List<String> gameNames;
}
