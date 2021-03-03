package de.timmi6790.mineplex_stats_api.versions.v1.java.groups;

import de.timmi6790.mineplex_stats_api.versions.v1.java.groups.models.GroupsModel;
import de.timmi6790.mineplex_stats_api.versions.v1.java.groups.repository.JavaGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JavaGroupService {
    private final JavaGroupRepository javaGroupRepository;

    @Autowired
    public JavaGroupService(final JavaGroupRepository javaGroupRepository) {
        this.javaGroupRepository = javaGroupRepository;
    }

    public List<GroupsModel> getGroups() {
        return this.javaGroupRepository.getGroups();
    }
}
