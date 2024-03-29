package de.timmi6790.mpstats.api.versions.v1.common.stat.repository.postgres;

import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.StatRepository;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.StatType;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.postgres.mappers.StatMapper;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.postgres.reducers.StatReducer;
import de.timmi6790.mpstats.api.versions.v1.common.utilities.PostgresRepository;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StatPostgresRepository extends PostgresRepository implements StatRepository {
    private final String addStatType;

    private final String getStats;
    private final String getStat;

    private final String insertStat;
    private final String removeStat;

    public StatPostgresRepository(final Jdbi database, final String schema) {
        super(database, schema);

        database
                .registerRowMapper(new StatMapper());

        // Create queries
        this.addStatType = this.formatQuery(QueryTemplates.ADD_STAT_TYPE);
        this.getStats = this.formatQuery(QueryTemplates.GET_STATS);
        this.getStat = this.formatQuery(QueryTemplates.GET_STAT);
        this.insertStat = this.formatQuery(QueryTemplates.INSERT_STAT);
        this.removeStat = this.formatQuery(QueryTemplates.REMOVE_STAT);
    }

    @Override
    public void addTypes(final StatType[] types) {
        this.getDatabase().useHandle(handle -> {
            final PreparedBatch batch = handle.prepareBatch(this.addStatType);
            for (final StatType type : types) {
                batch.bind("type", type.name());
                batch.add();
            }
            batch.execute();
        });
    }

    @Override
    public List<Stat> getStats() {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getStats)
                        .reduceRows(new StatReducer())
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Optional<Stat> getStat(final String statName) {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getStat)
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
        this.getDatabase().useHandle(handle ->
                handle.createUpdate(this.insertStat)
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
        this.getDatabase().useHandle(handle ->
                handle.createUpdate(this.removeStat)
                        .bind("statId", statId)
                        .execute()
        );
    }


    @Override
    public void setStatWebsiteName(final int statId, final String newWebsiteName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setStatName(final int statId, final String newStatName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setStatAchievement(final int statId, final boolean isAchievement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setStatDescription(final int statId, final String description) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addStatAlias(final int statId, final String aliasName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeStatAliases(final int statId, final String aliasName) {
        throw new UnsupportedOperationException();
    }

    private static class QueryTemplates {
        private static final String ADD_STAT_TYPE = "INSERT INTO $schema$.stat_types(type_name) " +
                "SELECT :type " +
                "WHERE " +
                "    NOT EXISTS ( " +
                "        SELECT id FROM $schema$.stat_types WHERE type_name = :type " +
                "    );";

        private static final String GET_STAT_BASE = "SELECT stat.\"id\" stat_id, stat.website_name website_name, stat.stat_name stat_name, stat.clean_name clean_name, " +
                "stat.description description, stat.achievement achievement, alias_name.alias_name alias_name, statType.type_name type_name, stat.sorting_priority sorting_priority " +
                "FROM $schema$.stats stat " +
                "INNER JOIN $schema$.stat_types statType ON statType.\"id\" = stat.type_id " +
                "LEFT JOIN $schema$.stat_alias alias_name ON alias_name.stat_id = stat.\"id\" " +
                "%s;";

        private static final String GET_STATS = String.format(GET_STAT_BASE, "");
        private static final String GET_STAT = String.format(GET_STAT_BASE, "WHERE LOWER(stat.stat_name) = LOWER(:statName)");

        private static final String INSERT_STAT = "INSERT INTO $schema$.stats(website_name, stat_name, clean_name, achievement) VALUES(:websiteName, :statName, :cleanName, :isAchievement);";
        private static final String REMOVE_STAT = "DELETE FROM $schema$.stats WHERE id = :statId;";
    }
}
