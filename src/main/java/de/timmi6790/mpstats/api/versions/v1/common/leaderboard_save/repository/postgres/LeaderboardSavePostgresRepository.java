package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.repository.postgres;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.models.PlayerData;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.repository.LeaderboardSaveRepository;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.repository.postgres.mappers.LeaderboardEntryMapper;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.repository.postgres.mappers.LeaderboardSaveDataMapper;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.repository.postgres.models.LeaderboardSaveData;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardSave;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.utilities.PostgresRepository;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;

import java.time.ZonedDateTime;
import java.util.*;

public class LeaderboardSavePostgresRepository<P extends Player> extends PostgresRepository implements LeaderboardSaveRepository<P> {
    private static final String LEADERBOARD_ID = "leaderboardId";

    private final LeaderboardEntryMapper<P> leaderboardEntryMapper;

    private final String insertLeaderboardSaveId;
    private final String insertLeaderboardSave;

    private final String getLeaderboardSaveTimes;
    private final String getLeaderboardSaveId;
    private final String getLeaderboardSaveIds;

    private final String getLeaderboardEntries;
    private final String getLeaderboardEntriesMulti;

    public LeaderboardSavePostgresRepository(final Jdbi database,
                                             final String schema,
                                             final PlayerService<P> playerService) {
        super(database, schema);

        this.getDatabase().registerRowMapper(new LeaderboardSaveDataMapper());

        this.leaderboardEntryMapper = new LeaderboardEntryMapper<>(playerService);

        // Create queries
        this.insertLeaderboardSaveId = this.formatQuery(QueryTemplates.INSERT_LEADERBOARD_SAVE_ID);
        this.insertLeaderboardSave = this.formatQuery(QueryTemplates.INSERT_LEADERBOARD_SAVE);

        this.getLeaderboardSaveTimes = this.formatQuery(QueryTemplates.GET_LEADERBOARD_SAVE_TIMES);
        this.getLeaderboardSaveId = this.formatQuery(QueryTemplates.GET_LEADERBOARD_SAVE_ID);
        this.getLeaderboardSaveIds = this.formatQuery(QueryTemplates.GET_LEADERBOARD_SAVE_IDS);

        this.getLeaderboardEntries = this.formatQuery(QueryTemplates.GET_LEADERBOARD_ENTRIES);
        this.getLeaderboardEntriesMulti = this.formatQuery(QueryTemplates.GET_LEADERBOARD_ENTRIES_MULTI);
    }

    private int insertSaveId(final Leaderboard leaderboard, final ZonedDateTime saveTime) {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.insertLeaderboardSaveId)
                        .bind(LEADERBOARD_ID, leaderboard.getRepositoryId())
                        .bind("saveTime", saveTime)
                        .mapTo(int.class)
                        .first()
        );
    }

    @Override
    public void saveLeaderboard(final Leaderboard leaderboard,
                                final List<PlayerData> entries,
                                final ZonedDateTime saveTime) {
        final int saveId = this.insertSaveId(leaderboard, saveTime);

        this.getDatabase().useHandle(handle -> {
            final PreparedBatch batch = handle.prepareBatch(this.insertLeaderboardSave);
            for (final PlayerData entry : entries) {
                batch.bind("leaderboardSaveId", saveId);
                batch.bind("playerId", entry.getPlayerRepositoryId());
                batch.bind("score", entry.getScore());
                batch.add();
            }
            batch.execute();
        });
    }

    @Override
    public List<ZonedDateTime> getLeaderboardSaveTimes(final Leaderboard leaderboard) {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getLeaderboardSaveTimes)
                        .bind(LEADERBOARD_ID, leaderboard.getRepositoryId())
                        .mapTo(ZonedDateTime.class)
                        .list()
        );
    }

    @Override
    public Optional<LeaderboardSave<P>> getLeaderboardEntries(final Leaderboard leaderboard,
                                                              final ZonedDateTime saveTime) {

        return this.getDatabase().withHandle(handle -> {
            final Optional<LeaderboardSaveData> saveDataOpt = handle.createQuery(this.getLeaderboardSaveId)
                    .bind(LEADERBOARD_ID, leaderboard.getRepositoryId())
                    .bind("requestedTime", saveTime)
                    .mapTo(LeaderboardSaveData.class)
                    .findFirst();

            if (saveDataOpt.isEmpty()) {
                return Optional.empty();
            }

            final List<LeaderboardEntry<P>> leaderboardEntries = handle.createQuery(this.getLeaderboardEntries)
                    .bind("saveId", saveDataOpt.get().getSaveId())
                    .scanResultSet(this.leaderboardEntryMapper)
                    .get(leaderboard.getRepositoryId());

            return Optional.of(
                    new LeaderboardSave<>(
                            saveDataOpt.get().getSaveTime(),
                            leaderboardEntries
                    )
            );
        });
    }

    @Override
    public Map<Leaderboard, LeaderboardSave<P>> getLeaderboardEntries(final Collection<Leaderboard> leaderboards,
                                                                      final ZonedDateTime saveTime) {
        if (leaderboards.isEmpty()) {
            return Collections.emptyMap();
        }

        final List<Integer> repositoryIds = Lists.newArrayListWithCapacity(leaderboards.size());
        for (final Leaderboard leaderboard : leaderboards) {
            repositoryIds.add(leaderboard.getRepositoryId());
        }

        return this.getDatabase().withHandle(handle -> {
            final List<LeaderboardSaveData> saveDataList = handle.createQuery(this.getLeaderboardSaveIds)
                    .bindList("leaderboardIds", repositoryIds)
                    .bind("requestedTime", saveTime)
                    .mapTo(LeaderboardSaveData.class)
                    .list();

            if (saveDataList.isEmpty()) {
                return Collections.emptyMap();
            }

            final List<Integer> saveIds = Lists.newArrayListWithCapacity(saveDataList.size());
            final Map<Integer, LeaderboardSaveData> idToSaveData = Maps.newHashMapWithExpectedSize(saveIds.size());
            for (final LeaderboardSaveData save : saveDataList) {
                saveIds.add(save.getSaveId());
                idToSaveData.put(save.getLeaderboardId(), save);
            }

            final Map<Integer, List<LeaderboardEntry<P>>> entries = handle.createQuery(this.getLeaderboardEntriesMulti)
                    .bindList("saveIds", saveIds)
                    .scanResultSet(this.leaderboardEntryMapper);

            final Map<Leaderboard, LeaderboardSave<P>> parsedSaves = Maps.newHashMapWithExpectedSize(leaderboards.size());
            for (final Leaderboard leaderboard : leaderboards) {
                final List<LeaderboardEntry<P>> entryList = entries.get(leaderboard.getRepositoryId());
                final LeaderboardSaveData saveData = idToSaveData.get(leaderboard.getRepositoryId());
                if (entryList != null) {
                    parsedSaves.put(
                            leaderboard,
                            new LeaderboardSave<>(
                                    saveData.getSaveTime(),
                                    entryList
                            )
                    );
                }
            }

            return parsedSaves;
        });
    }

    private static class QueryTemplates {
        private static final String INSERT_LEADERBOARD_SAVE_ID = "INSERT INTO $schema$.leaderboard_save_ids(leaderboard_id, save_time) VALUES(:leaderboardId, :saveTime) RETURNING id;";
        private static final String INSERT_LEADERBOARD_SAVE = "INSERT INTO $schema$.leaderboard_saves(leaderboard_save_id, player_id, score) VALUES(:leaderboardSaveId, :playerId, :score);";
        private static final String GET_LEADERBOARD_SAVE_TIMES = "SELECT save_time FROM $schema$.leaderboard_save_ids WHERE leaderboard_id = :leaderboardId;";

        private static final String GET_LEADERBOARD_SAVE_ID_BASE = "SELECT DISTINCT ON (leaderboard_id) leaderboard_id, leaderboard_save_ids.id saveId, save_time " +
                "FROM $schema$.leaderboard_save_ids " +
                "WHERE %s " +
                "ORDER BY leaderboard_id, ABS (EXTRACT(EPOCH FROM save_time) - EXTRACT(EPOCH FROM :requestedTime::timestamptz))";
        private static final String GET_LEADERBOARD_SAVE_ID = String.format(
                GET_LEADERBOARD_SAVE_ID_BASE,
                "leaderboard_id = :leaderboardId"
        );
        private static final String GET_LEADERBOARD_SAVE_IDS = String.format(
                GET_LEADERBOARD_SAVE_ID_BASE,
                "leaderboard_id IN (<leaderboardIds>)"
        );

        private static final String GET_LEADERBOARD_ENTRIES_BASE = "SELECT saveIds.leaderboard_id, player_id, score " +
                "FROM $schema$.leaderboard_saves saves " +
                "INNER JOIN $schema$.leaderboard_save_ids saveIds ON saveIds.\"id\" = saves.leaderboard_save_id " +
                "WHERE %s " +
                "ORDER BY score DESC;";
        private static final String GET_LEADERBOARD_ENTRIES = String.format(
                GET_LEADERBOARD_ENTRIES_BASE,
                "saves.leaderboard_save_id = :saveId"
        );
        private static final String GET_LEADERBOARD_ENTRIES_MULTI = String.format(
                GET_LEADERBOARD_ENTRIES_BASE,
                "saves.leaderboard_save_id IN (<saveIds>)"
        );
    }
}
