package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.repository.postgres;

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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class LeaderboardSavePostgresRepository<P extends Player> extends PostgresRepository implements LeaderboardSaveRepository<P> {
    private final LeaderboardEntryMapper<P> leaderboardEntryMapper;

    private final String insertLeaderboardSaveId;
    private final String insertLeaderboardSave;

    private final String getLeaderboardSaveTimes;
    private final String getLeaderboardSaveId;

    private final String getLeaderboardEntries;

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

        this.getLeaderboardEntries = this.formatQuery(QueryTemplates.GET_LEADERBOARD_ENTRIES);
    }

    private int insertSaveId(final Leaderboard leaderboard, final LocalDateTime saveTime) {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.insertLeaderboardSaveId)
                        .bind("leaderboardId", leaderboard.getRepositoryId())
                        .bind("saveTime", saveTime)
                        .mapTo(int.class)
                        .first()
        );
    }

    private Optional<LeaderboardSaveData> getLeaderboardSaveId(final Leaderboard leaderboard,
                                                               final LocalDateTime saveTime) {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getLeaderboardSaveId)
                        .bind("leaderboardId", leaderboard.getRepositoryId())
                        .bind("requestedTime", saveTime)
                        .mapTo(LeaderboardSaveData.class)
                        .findFirst()
        );
    }

    @Override
    public void saveLeaderboard(final Leaderboard leaderboard,
                                final List<PlayerData> entries,
                                final LocalDateTime saveTime) {
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
    public List<LocalDateTime> getLeaderboardSaveTimes(final Leaderboard leaderboard) {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getLeaderboardSaveTimes)
                        .bind("leaderboardId", leaderboard.getRepositoryId())
                        .mapTo(LocalDateTime.class)
                        .list()
        );
    }

    @Override
    public Optional<LeaderboardSave<P>> getLeaderboardEntries(final Leaderboard leaderboard,
                                                              final LocalDateTime saveTime) {
        final Optional<LeaderboardSaveData> saveDataOpt = this.getLeaderboardSaveId(leaderboard, saveTime);
        if (saveDataOpt.isEmpty()) {
            return Optional.empty();
        }

        final LeaderboardSaveData saveData = saveDataOpt.get();
        final List<LeaderboardEntry<P>> leaderboardEntries = this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getLeaderboardEntries)
                        .bind("saveId", saveData.getSaveId())
                        .map(this.leaderboardEntryMapper)
                        .list()
        );

        return Optional.of(
                new LeaderboardSave<>(
                        saveData.getSaveTime(),
                        leaderboardEntries
                )
        );
    }

    private static class QueryTemplates {
        private static final String INSERT_LEADERBOARD_SAVE_ID = "INSERT INTO $schema$.leaderboard_save_ids(leaderboard_id, save_time) VALUES(:leaderboardId, :saveTime) RETURNING id;";
        private static final String INSERT_LEADERBOARD_SAVE = "INSERT INTO $schema$.leaderboard_saves(leaderboard_save_id, player_id, score) VALUES(:leaderboardSaveId, :playerId, :score);";
        private static final String GET_LEADERBOARD_SAVE_TIMES = "SELECT save_time FROM $schema$.leaderboard_save_ids WHERE leaderboard_id = :leaderboardId;";
        private static final String GET_LEADERBOARD_SAVE_ID = "SELECT id, save_time " +
                "FROM $schema$.leaderboard_save_ids " +
                "WHERE leaderboard_id = :leaderboardId " +
                "ORDER BY ABS(EXTRACT(EPOCH FROM save_time) - EXTRACT(EPOCH FROM :requestedTime::timestamptz)) " +
                "LIMIT 1;";
        private static final String GET_LEADERBOARD_ENTRIES = "SELECT player_id, score " +
                "FROM $schema$.leaderboard_saves saves " +
                "WHERE saves.leaderboard_save_id = :saveId " +
                "ORDER BY score DESC;";
    }
}
