package de.timmi6790.mpstats.api.versions.v1.java.stat.repository.postgres;

import de.timmi6790.mpstats.api.versions.v1.java.stat.repository.JavaStatRepository;
import de.timmi6790.mpstats.api.versions.v1.java.stat.repository.models.Stat;
import de.timmi6790.mpstats.api.versions.v1.java.stat.repository.postgres.mappers.StatMapper;
import de.timmi6790.mpstats.api.versions.v1.java.stat.repository.postgres.reducers.StatReducer;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JavaStatPostgresRepository implements JavaStatRepository {
    private static final String GET_STAT_BASE = "SELECT stat.\"id\" stat_id, stat.website_name website_name, stat.stat_name stat_name, stat.clean_name clean_name, stat.description description, stat.achievement achievement, alias_name.alias_name alias_name " +
            "FROM java.stats stat " +
            "LEFT JOIN java.stat_alias alias_name ON alias_name.stat_id = stat.\"id\" " +
            "%s;";

    private static final String GET_STATS = String.format(GET_STAT_BASE, "");
    private static final String GET_STAT = String.format(GET_STAT_BASE, "WHERE LOWER(stat.stat_name) = LOWER(:statName)");

    private static final String INSERT_STAT = "INSERT INTO java.stats(website_name, stat_name, clean_name, achievement) VALUES(:websiteName, :statName, :cleanName, :isAchievement);";
    private static final String REMOVE_STAT = "DELETE FROM java.stats WHERE id = :statId;";

    private final Jdbi database;

    @Autowired
    public JavaStatPostgresRepository(final Jdbi database) {
        this.database = database;

        database.registerRowMapper(new StatMapper());
    }

    @Override
    public List<Stat> getStats() {
        return this.database.withHandle(handle ->
                handle.createQuery(GET_STATS)
                        .reduceRows(new StatReducer())
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Optional<Stat> getStat(final String statName) {
        return this.database.withHandle(handle ->
                handle.createQuery(GET_STAT)
                        .bind("statName", statName)
                        .reduceRows(new StatReducer())
                        .findFirst()
        );
    }

    @Override
    public Stat createStat(final String websiteName,
                           final String statName,
                           final String cleanName,
                           final boolean isAchievement) {
        this.database.useHandle(handle ->
                handle.createUpdate(INSERT_STAT)
                        .bind("websiteName", websiteName)
                        .bind("statName", statName)
                        .bind("cleanName", cleanName)
                        .bind("isAchievement", isAchievement)
                        .execute()
        );

        return this.getStat(statName).orElseThrow(RuntimeException::new);
    }

    @Override
    public void removeStat(final int statId) {
        this.database.useHandle(handle ->
                handle.createUpdate(REMOVE_STAT)
                        .bind("statId", statId)
                        .execute()
        );
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
