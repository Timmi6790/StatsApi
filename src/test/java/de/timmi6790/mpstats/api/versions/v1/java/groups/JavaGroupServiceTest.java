package de.timmi6790.mpstats.api.versions.v1.java.groups;

import de.timmi6790.mpstats.api.AbstractIntegrationTest;
import de.timmi6790.mpstats.api.versions.v1.java.groups.repository.JavaGroupRepository;
import de.timmi6790.mpstats.api.versions.v1.java.groups.repository.models.Group;
import de.timmi6790.mpstats.api.versions.v1.java.groups.repository.postgres.JavaGroupPostgresRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class JavaGroupServiceTest {
    private static JavaGroupRepository javaGroupRepository;
    private static JavaGroupService javaGroupService;

    private static final AtomicInteger GROUP_ID = new AtomicInteger(0);

    @BeforeAll
    static void setUp() {
        javaGroupRepository = new JavaGroupPostgresRepository(AbstractIntegrationTest.jdbi());
        javaGroupService = new JavaGroupService(javaGroupRepository);
    }

    private String generateGroupName() {
        return "GROUP" + GROUP_ID.incrementAndGet();
    }

    @Test
    void createGroup() {
        final String groupName = this.generateGroupName();

        // Insert group
        final Group groupCreate = javaGroupService.createGroup(groupName, groupName);
        assertThat(groupCreate.groupName()).isEqualTo(groupName);

        // Verify cache
        final Optional<Group> groupCache = javaGroupService.getGroup(groupName);
        assertThat(groupCache).isPresent();
        assertThat(groupCache.get().groupName()).isEqualTo(groupName);

        // Verify none cache
        final Optional<Group> groupNoCache = javaGroupRepository.getGroup(groupName);
        assertThat(groupNoCache)
                .isPresent()
                .isEqualTo(groupCache);
    }

    @Test
    void createGroup_duplicate() {
        final String groupName = this.generateGroupName();

        // Insert group
        final Group group = javaGroupService.createGroup(groupName, groupName);
        final Group groupDuplicate = javaGroupService.createGroup(groupName, groupName);

        assertThat(group).isEqualTo(groupDuplicate);
    }

    @Test
    void createGroup_ignore_case() {
        final String groupName = this.generateGroupName();

        // Insert group
        final Group group = javaGroupService.createGroup(groupName, groupName);

        // Lower check
        final Group groupLower = javaGroupService.createGroup(groupName.toLowerCase(), groupName);
        assertThat(group).isEqualTo(groupLower);

        // Upper check
        final Group groupUpper = javaGroupService.createGroup(groupName.toUpperCase(), groupName);
        assertThat(group).isEqualTo(groupUpper);
    }

    @Test
    void deleteGroup() {
        final String groupName = this.generateGroupName();

        javaGroupService.createGroup(groupName, groupName);
        javaGroupService.deleteGroup(groupName);

        // Verify cache
        final Optional<Group> groupCache = javaGroupService.getGroup(groupName);
        assertThat(groupCache).isNotPresent();

        // Verify none cache
        final Optional<Group> groupNoCache = javaGroupRepository.getGroup(groupName);
        assertThat(groupNoCache).isNotPresent();
    }

    @Test
    void getGroup() {
        final String groupName = this.generateGroupName();

        // Assure that the group does not exist
        final Optional<Group> groupNotFound = javaGroupService.getGroup(groupName);
        assertThat(groupNotFound).isNotPresent();

        // Create group
        javaGroupService.createGroup(groupName, groupName);

        final Optional<Group> groupFound = javaGroupService.getGroup(groupName);
        assertThat(groupFound).isPresent();

        // Verify none cache
        final Optional<Group> groupNoCache = javaGroupRepository.getGroup(groupName);
        assertThat(groupNoCache).isPresent();
    }

    @Test
    void getGroup_ignore_case() {
        final String groupName = this.generateGroupName();
        final Group group = javaGroupService.createGroup(groupName, groupName);

        final Optional<Group> groupLower = javaGroupService.getGroup(groupName.toLowerCase());
        assertThat(groupLower)
                .isPresent()
                .contains(group);

        final Optional<Group> groupUpper = javaGroupService.getGroup(groupName.toUpperCase());
        assertThat(groupUpper)
                .isPresent()
                .contains(group);
    }

    @Test
    void getGroups() {
        final String groupName1 = this.generateGroupName();
        final String groupName2 = this.generateGroupName();

        // Assure that the groups does not exist
        final List<String> groupsNotContains = javaGroupService.getGroups()
                .stream()
                .map(Group::groupName)
                .collect(Collectors.toList());
        assertThat(groupsNotContains).doesNotContain(groupName1, groupName2);

        // Create groups
        javaGroupService.createGroup(groupName1, groupName1);
        javaGroupService.createGroup(groupName2, groupName2);

        final List<String> groupsContains = javaGroupService.getGroups()
                .stream()
                .map(Group::groupName)
                .collect(Collectors.toList());
        assertThat(groupsContains).contains(groupName1, groupName2);
    }

    @Test
    void hasGroup() {
        final String groupName = this.generateGroupName();

        final boolean groupNotFound = javaGroupService.hasGroup(groupName);
        assertThat(groupNotFound).isFalse();

        // Create group
        javaGroupService.createGroup(groupName, groupName);

        final boolean groupFound = javaGroupService.hasGroup(groupName);
        assertThat(groupFound).isTrue();

        // Delete group
        javaGroupService.deleteGroup(groupName);

        final boolean groupNotFoundDeleted = javaGroupService.hasGroup(groupName);
        assertThat(groupNotFoundDeleted).isFalse();
    }
}