package de.timmi6790.mpstats.api.versions.v1.common.group;

import de.timmi6790.mpstats.api.versions.v1.common.group.repository.GroupRepository;
import de.timmi6790.mpstats.api.versions.v1.common.group.repository.models.Group;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractGroupServiceTest {
    private static final AtomicInteger GROUP_ID = new AtomicInteger(0);

    private final Supplier<GroupService> groupServiceSupplier;

    private final GroupService groupService;
    private final GroupRepository groupRepository;

    public AbstractGroupServiceTest(final Supplier<GroupService> groupServiceSupplier) {
        this.groupServiceSupplier = groupServiceSupplier;
        this.groupService = groupServiceSupplier.get();
        this.groupRepository = this.groupService.getGroupRepository();
    }

    private String generateGroupName() {
        return "GROUP" + GROUP_ID.incrementAndGet();
    }

    @Test
    void createGroup() {
        final String groupName = this.generateGroupName();

        // Insert group
        final Group groupCreate = this.groupService.createGroup(groupName, groupName);
        assertThat(groupCreate.getGroupName()).isEqualTo(groupName);

        // Verify cache
        final Optional<Group> groupCache = this.groupService.getGroup(groupName);
        assertThat(groupCache).isPresent();
        assertThat(groupCache.get().getGroupName()).isEqualTo(groupName);

        // Verify none cache
        final Optional<Group> groupNoCache = this.groupRepository.getGroup(groupName);
        assertThat(groupNoCache)
                .isPresent()
                .isEqualTo(groupCache);
    }

    @Test
    void createGroup_duplicate() {
        final String groupName = this.generateGroupName();

        // Insert group
        final Group group = this.groupService.createGroup(groupName, groupName);
        final Group groupDuplicate = this.groupService.createGroup(groupName, groupName);

        assertThat(group).isEqualTo(groupDuplicate);
    }

    @Test
    void createGroup_ignore_case() {
        final String groupName = this.generateGroupName();

        // Insert group
        final Group group = this.groupService.createGroup(groupName, groupName);

        // Lower check
        final Group groupLower = this.groupService.createGroup(groupName.toLowerCase(), groupName);
        assertThat(group).isEqualTo(groupLower);

        // Upper check
        final Group groupUpper = this.groupService.createGroup(groupName.toUpperCase(), groupName);
        assertThat(group).isEqualTo(groupUpper);
    }

    @Test
    void deleteGroup() {
        final String groupName = this.generateGroupName();

        this.groupService.createGroup(groupName, groupName);
        this.groupService.deleteGroup(groupName);

        // Verify cache
        final Optional<Group> groupCache = this.groupService.getGroup(groupName);
        assertThat(groupCache).isNotPresent();

        // Verify none cache
        final Optional<Group> groupNoCache = this.groupRepository.getGroup(groupName);
        assertThat(groupNoCache).isNotPresent();
    }

    @Test
    void getGroup() {
        final String groupName = this.generateGroupName();

        // Assure that the group does not exist
        final Optional<Group> groupNotFound = this.groupService.getGroup(groupName);
        assertThat(groupNotFound).isNotPresent();

        // Create group
        this.groupService.createGroup(groupName, groupName);

        final Optional<Group> groupFound = this.groupService.getGroup(groupName);
        assertThat(groupFound).isPresent();

        // Verify none cache
        final Optional<Group> groupNoCache = this.groupRepository.getGroup(groupName);
        assertThat(groupNoCache).isPresent();
    }

    @Test
    void getGroup_ignore_case() {
        final String groupName = this.generateGroupName();
        final Group group = this.groupService.createGroup(groupName, groupName);

        final Optional<Group> groupLower = this.groupService.getGroup(groupName.toLowerCase());
        assertThat(groupLower)
                .isPresent()
                .contains(group);

        final Optional<Group> groupUpper = this.groupService.getGroup(groupName.toUpperCase());
        assertThat(groupUpper)
                .isPresent()
                .contains(group);
    }

    @Test
    void getGroups() {
        final String groupName1 = this.generateGroupName();
        final String groupName2 = this.generateGroupName();

        // Assure that the groups does not exist
        final List<String> groupsNotContains = this.groupService.getGroups()
                .stream()
                .map(Group::getGroupName)
                .collect(Collectors.toList());
        assertThat(groupsNotContains).doesNotContain(groupName1, groupName2);

        // Create groups
        this.groupService.createGroup(groupName1, groupName1);
        this.groupService.createGroup(groupName2, groupName2);

        final List<String> groupsContains = this.groupService.getGroups()
                .stream()
                .map(Group::getGroupName)
                .collect(Collectors.toList());
        assertThat(groupsContains).contains(groupName1, groupName2);
    }

    @Test
    void hasGroup() {
        final String groupName = this.generateGroupName();

        final boolean groupNotFound = this.groupService.hasGroup(groupName);
        assertThat(groupNotFound).isFalse();

        // Create group
        this.groupService.createGroup(groupName, groupName);

        final boolean groupFound = this.groupService.hasGroup(groupName);
        assertThat(groupFound).isTrue();

        // Delete group
        this.groupService.deleteGroup(groupName);

        final boolean groupNotFoundDeleted = this.groupService.hasGroup(groupName);
        assertThat(groupNotFoundDeleted).isFalse();
    }
}