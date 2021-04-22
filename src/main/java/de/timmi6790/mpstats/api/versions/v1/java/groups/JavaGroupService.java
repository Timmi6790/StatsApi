package de.timmi6790.mpstats.api.versions.v1.java.groups;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.util.concurrent.Striped;
import de.timmi6790.mpstats.api.versions.v1.java.groups.repository.JavaGroupRepository;
import de.timmi6790.mpstats.api.versions.v1.java.groups.repository.models.Group;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Service
@Log4j2
public class JavaGroupService {
    private final JavaGroupRepository javaGroupRepository;

    private final Striped<Lock> groupLock = Striped.lock(32);

    private final Set<String> groupNames = Collections.synchronizedSet(new TreeSet<>(String.CASE_INSENSITIVE_ORDER));

    private final Cache<String, Group> groupCache = Caffeine.newBuilder()
            .expireAfterAccess(5, TimeUnit.SECONDS)
            .build();

    @Autowired
    public JavaGroupService(final JavaGroupRepository javaGroupRepository) {
        this.javaGroupRepository = javaGroupRepository;

        for (final Group group : javaGroupRepository.getGroups()) {
            this.groupNames.add(group.getGroupName());
        }
    }

    private Lock getGroupLock(final String groupName) {
        return this.groupLock.get(groupName.toLowerCase());
    }

    private Optional<Group> getGroupFromCache(final String groupName) {
        return Optional.ofNullable(this.groupCache.getIfPresent(groupName.toLowerCase()));
    }

    private void insertGroupIntoCache(final Group group) {
        log.debug("Add group to cache: {}", group);
        this.groupCache.put(group.getGroupName().toLowerCase(), group);
    }

    private void invalidateGroupCache(final String groupName) {
        this.groupCache.invalidate(groupName.toLowerCase());
    }

    public boolean hasGroup(final String groupName) {
        return this.groupNames.contains(groupName);
    }

    public Group createGroup(final String groupName, final String cleanName) {
        final Lock lock = this.getGroupLock(groupName);
        lock.lock();

        try {
            if (this.hasGroup(groupName)) {
                return this.getGroup(groupName).orElseThrow(RuntimeException::new);
            }

            log.info("Creating group {}", groupName);
            final Group group = this.javaGroupRepository.createGroup(groupName, cleanName);
            this.insertGroupIntoCache(group);
            this.groupNames.add(group.getGroupName());
            return group;
        } finally {
            lock.unlock();
        }
    }

    public void deleteGroup(final String groupName) {
        final Lock lock = this.getGroupLock(groupName);
        lock.lock();

        try {
            final Optional<Group> groupOpt = this.getGroup(groupName);
            if (groupOpt.isPresent()) {
                log.info("Deleting group {}", groupOpt.get());
                this.javaGroupRepository.deleteGroup(groupOpt.get().getRepositoryId());
                this.groupNames.remove(groupOpt.get().getGroupName());
                this.invalidateGroupCache(groupName);
            }
        } finally {
            lock.unlock();
        }
    }

    public Optional<Group> getGroup(final String groupName) {
        // TODO: Check for group name and alias names. Add full alias support into the api

        final Optional<Group> groupCached = this.getGroupFromCache(groupName);
        if (groupCached.isPresent()) {
            return groupCached;
        }

        final Optional<Group> groupOpt = this.javaGroupRepository.getGroup(groupName);
        log.debug("Get group from repository: {}", groupOpt);
        if (groupOpt.isPresent()) {
            this.insertGroupIntoCache(groupOpt.get());
            return groupOpt;
        }

        return Optional.empty();
    }

    public List<Group> getGroups() {
        return this.javaGroupRepository.getGroups();
    }
}
