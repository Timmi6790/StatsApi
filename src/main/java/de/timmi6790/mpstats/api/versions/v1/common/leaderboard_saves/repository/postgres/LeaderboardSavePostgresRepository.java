package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_saves.repository.postgres;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_saves.models.PlayerData;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_saves.repository.LeaderboardSaveRepository;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_saves.repository.models.LeaderboardEntryMapper;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.RepositoryPlayer;
import de.timmi6790.mpstats.api.versions.v1.common.utilities.PostgresRepository;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class LeaderboardSavePostgresRepository<R extends Player & RepositoryPlayer> extends PostgresRepository implements LeaderboardSaveRepository<R> {
    private final LeaderboardEntryMapper<R> leaderboardEntryMapper;

    private final String insertLeaderboardSaveId;
    private final String insertLeaderboardSave;

    private final String getLeaderboardSaveIds;
    private final String getLeaderboardSaveId;

    private final String getLeaderboardEntries;

    public LeaderboardSavePostgresRepository(final Jdbi database,
                                             final String schema,
                                             final PlayerService<R> playerService) {
        super(database, schema);

        this.leaderboardEntryMapper = new LeaderboardEntryMapper<>(playerService);

        // Create queries
        this.insertLeaderboardSaveId = this.formatQuery(QueryTemplates.INSERT_LEADERBOARD_SAVE_ID);
        this.insertLeaderboardSave = this.formatQuery(QueryTemplates.INSERT_LEADERBOARD_SAVE);

        this.getLeaderboardSaveIds = this.formatQuery(QueryTemplates.GET_LEADERBOARD_SAVE_IDS);
        this.getLeaderboardSaveId = this.formatQuery(QueryTemplates.GET_LEADERBOARD_SAVE_ID);

        this.getLeaderboardEntries = this.formatQuery(QueryTemplates.GET_LEADERBOARD_ENTRIES);
    }

    private int insertSaveId(final Leaderboard leaderboard, final LocalDateTime saveTime) {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.insertLeaderboardSaveId)
                        .bind("leaderboardId", leaderboard.repositoryId())
                        .bind("saveTime", saveTime)
                        .mapTo(int.class)
                        .first()
        );
    }

    private Optional<Integer> getLeaderboardSaveId(final Leaderboard leaderboard, final LocalDateTime saveTime) {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getLeaderboardSaveId)
                        .bind("leaderboardId", leaderboard.repositoryId())
                        .bind("requestedTime", saveTime)
                        .mapTo(int.class)
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
                batch.bind("playerId", entry.getRepositoryId());
                batch.bind("score", entry.score());
                batch.add();
            }
            batch.execute();
        });
    }

    @Override
    public List<LocalDateTime> getLeaderboardSaveTimes(final Leaderboard leaderboard) {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getLeaderboardSaveIds)
                        .bind("leaderboardId", leaderboard.repositoryId())
                        .mapTo(LocalDateTime.class)
                        .list()
        );
    }

    @Override
    public Optional<List<LeaderboardEntry<R>>> getLeaderboardEntries(final Leaderboard leaderboard,
                                                                     final LocalDateTime saveTime) {
        final Optional<Integer> saveId = this.getLeaderboardSaveId(leaderboard, saveTime);
        if (saveId.isEmpty()) {
            return Optional.empty();
        }

        final List<LeaderboardEntry<R>> leaderboardEntries = this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getLeaderboardEntries)
                        .bind("saveId", saveId.get())
                        .map(this.leaderboardEntryMapper)
                        .list()
        );

        return Optional.of(leaderboardEntries);
    }

    private static class QueryTemplates {
        private static final String INSERT_LEADERBOARD_SAVE_ID = "INSERT INTO $schema$.leaderboard_save_ids(leaderboard_id, save_time) VALUES(:leaderboardId, :saveTime) RETURNING id;";
        private static final String INSERT_LEADERBOARD_SAVE = "INSERT INTO $schema$.leaderboard_saves(leaderboard_save_id, player_id, score) VALUES(:leaderboardSaveId, :playerId, :score);";
        private static final String GET_LEADERBOARD_SAVE_IDS = "SELECT save_time FROM $schema$.leaderboard_save_ids WHERE leaderboard_id = :leaderboardId;";
        private static final String GET_LEADERBOARD_SAVE_ID = "SELECT id " +
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
