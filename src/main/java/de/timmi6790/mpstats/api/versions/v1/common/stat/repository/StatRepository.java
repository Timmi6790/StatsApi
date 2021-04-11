package de.timmi6790.mpstats.api.versions.v1.common.stat.repository;

import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;

import java.util.List;
import java.util.Optional;

public interface StatRepository {
    List<Stat> getStats();

    Optional<Stat> getStat(String statName);

    Stat createStat(String websiteName, String statName, String cleanName, boolean isAchievement);

    void removeStat(int statId);

    void setStatWebsiteName(int statId, String newWebsiteName);

    void setStatName(int statId, String newStatName);

    void setStatAchievement(int statId, boolean isAchievement);

    void setStatDescription(int statId, String description);

    void addStatAlias(int statId, String aliasName);

    void removeStatAliases(int statId, String aliasName);
}
