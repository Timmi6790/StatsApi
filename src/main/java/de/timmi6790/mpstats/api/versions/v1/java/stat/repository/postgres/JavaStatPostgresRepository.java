package de.timmi6790.mpstats.api.versions.v1.java.stat.repository.postgres;

import de.timmi6790.mpstats.api.versions.v1.java.stat.repository.JavaStatRepository;
import de.timmi6790.mpstats.api.versions.v1.java.stat.repository.models.Stat;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JavaStatPostgresRepository implements JavaStatRepository {
    @Override
    public List<Stat> getStats() {
        return null;
    }

    @Override
    public Optional<Stat> getStat(final String statName) {
        return Optional.empty();
    }

    @Override
    public Stat createStat(final String websiteName, final String statName, final boolean isAchievement) {
        return null;
    }

    @Override
    public void removeStat(final int statId) {

    }

    @Override
    public void setStatWebsiteName(final int statId, final String newWebsiteName) {

    }

    @Override
    public void setStatName(final int statId, final String newStatName) {

    }

    @Override
    public void setStatAchievement(final int statId, final boolean isAchievement) {

    }

    @Override
    public void setStatDescription(final int statId, final String description) {

    }

    @Override
    public void addStatAlias(final int statId, final String aliasName) {

    }

    @Override
    public void removeStatAliases(final int statId, final String aliasName) {

    }
}
