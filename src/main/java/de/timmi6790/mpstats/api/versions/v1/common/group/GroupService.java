package de.timmi6790.mpstats.api.versions.v1.common.group;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.util.concurrent.Striped;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.group.repository.GroupRepository;
import de.timmi6790.mpstats.api.versions.v1.common.group.repository.models.Group;
import de.timmi6790.mpstats.api.versions.v1.common.group.repository.postgres.GroupPostgresRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jdbi.v3.core.Jdbi;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Log4j2
public class GroupService {
    @Getter(AccessLevel.PROTECTED)
    private final GroupRepository groupRepository;

    private final Striped<Lock> groupLock = Striped.lock(32);

    private final Set<String> groupNames = Collections.synchronizedSet(new TreeSet<>(String.CASE_INSENSITIVE_ORDER));
    private final Map<String, String> aliasNames = new LinkedCaseInsensitiveMap<>();

    private final Cache<String, Group> groupCache = Caffeine.newBuilder()
            .expireAfterAccess(5, TimeUnit.SECONDS)
            .build();

    protected GroupService(final Jdbi database, final String schema, final GameService gameService) {
        this.groupRepository = new GroupPostgresRepository(database, schema, gameService);

        for (final Group group : this.groupRepository.getGroups()) {
            this.groupNames.add(group.getGroupName());

            for (final String aliasName : group.getAliasNames()) {
                this.aliasNames.put(aliasName, group.getGroupName());
            }
        }
    }

    private Lock getGroupLock(final String groupName) {
        return this.groupLock.get(groupName.toLowerCase());
    }

    private Optional<Group> getGroupFromCache(final String groupName) {
        return Optional.ofNullable(this.groupCache.getIfPresent(groupName.toLowerCase()));
    }

    private String getGroupName(final String groupName) {
        return this.aliasNames.getOrDefault(groupName, groupName);
    }

    private void insertGroupIntoCache(final Group group) {
        log.debug("Add group to cache: {}", group);
        this.groupCache.put(group.getGroupName().toLowerCase(), group);
    }

    private void invalidateGroupCache(final String groupName) {
        this.groupCache.invalidate(groupName.toLowerCase());
    }

    public boolean hasGroup(final String groupName) {
        return this.groupNames.contains(this.getGroupName(groupName));
    }

    public Group createGroup(String groupName, final String cleanName) {
        groupName = this.getGroupName(groupName);
        final Lock lock = this.getGroupLock(groupName);
        lock.lock();

        try {
            if (this.hasGroup(groupName)) {
                return this.getGroup(groupName).orElseThrow(RuntimeException::new);
            }

            log.info("Creating group {}", groupName);
            final Group group = this.groupRepository.createGroup(groupName, cleanName);
            this.insertGroupIntoCache(group);
            this.groupNames.add(group.getGroupName());
            return group;
        } finally {
            lock.unlock();
        }
    }

    public void deleteGroup(String groupName) {
        groupName = this.getGroupName(groupName);
        final Lock lock = this.getGroupLock(groupName);
        lock.lock();

        try {
            final Optional<Group> groupOpt = this.getGroup(groupName);
            if (groupOpt.isPresent()) {
                log.info("Deleting group {}", groupOpt.get());
                this.groupRepository.deleteGroup(groupOpt.get().getRepositoryId());
                this.groupNames.remove(groupOpt.get().getGroupName());
                this.invalidateGroupCache(groupName);
            }
        } finally {
            lock.unlock();
        }
    }

    public Optional<Group> getGroup(String groupName) {
        groupName = this.getGroupName(groupName);
        final Optional<Group> groupCached = this.getGroupFromCache(groupName);
        if (groupCached.isPresent()) {
            return groupCached;
        }

        final Optional<Group> groupOpt = this.groupRepository.getGroup(groupName);
        log.debug("Get group from repository: {}", groupOpt);
        if (groupOpt.isPresent()) {
            this.insertGroupIntoCache(groupOpt.get());
            return groupOpt;
        }

        return Optional.empty();
    }

    public List<Group> getGroups() {
        return this.groupRepository.getGroups();
    }
}
