package de.timmi6790.mpstats.api.versions.v1.common.filter.repository.postgres;

import de.timmi6790.mpstats.api.versions.v1.common.filter.models.Reason;
import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.FilterRepository;
import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models.Filter;
import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.postgres.mappers.FilterMapper;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.utilities.PostgresRepository;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

public class FilterPostgresRepository<P extends Player> extends PostgresRepository implements FilterRepository<P> {
    private static final String PLAYER_ID = "playerId";
    private static final String LEADERBOARD_ID = "leaderboardId";

    private final String addFilterReason;

    private final String insertFilter;
    private final String removeFilter;

    private final String getFilters;
    private final String getFiltersByPlayerId;
    private final String getFiltersByLeaderboardId;
    private final String getFiltersByPlayerLeaderboardTime;
    private final String getFiltersByPlayerLeaderboard;

    private final FilterMapper<P> filterMapper;

    public FilterPostgresRepository(final PlayerService<P> playerService,
                                    final LeaderboardService leaderboardService,
                                    final Jdbi database,
                                    final String schema) {
        super(database, schema);

        this.filterMapper = new FilterMapper<>(playerService, leaderboardService);

        // Create queries
        this.addFilterReason = this.formatQuery(QueryTemplates.ADD_FILTER_REASON);

        this.insertFilter = this.formatQuery(QueryTemplates.INSERT_FILTER);
        this.removeFilter = this.formatQuery(QueryTemplates.REMOVE_FILTER);

        this.getFilters = this.formatQuery(QueryTemplates.GET_FILTERS);
        this.getFiltersByPlayerId = this.formatQuery(QueryTemplates.GET_FILTERS_BY_PLAYER_ID);
        this.getFiltersByLeaderboardId = this.formatQuery(QueryTemplates.GET_FILTERS_BY_LEADERBOARD_ID);
        this.getFiltersByPlayerLeaderboardTime = this.formatQuery(QueryTemplates.GET_FILTERS_BY_PLAYER_LEADERBOARD_TIME);
        this.getFiltersByPlayerLeaderboard = this.formatQuery(QueryTemplates.GET_FILTERS_BY_PLAYER_LEADERBOARD);
    }

    @Override
    public void addFilterReasons(final Collection<String> filterReasons) {
        this.getDatabase().useHandle(handle -> {
            final PreparedBatch batch = handle.prepareBatch(this.addFilterReason);
            for (final String reason : filterReasons) {
                batch.bind("reason", reason);
                batch.add();
            }
            batch.execute();
        });
    }

    @Override
    public List<Filter<P>> getFilters() {
        return this.getDatabase().withHandle(handler ->
                handler.createQuery(this.getFilters)
                        .scanResultSet(this.filterMapper)
        );
    }

    @Override
    public List<Filter<P>> getFilters(final P player) {
        return this.getDatabase().withHandle(handler ->
                handler.createQuery(this.getFiltersByPlayerId)
                        .bind(PLAYER_ID, player.getRepositoryId())
                        .scanResultSet(this.filterMapper)
        );
    }

    @Override
    public List<Filter<P>> getFilters(final Leaderboard leaderboard) {
        return this.getDatabase().withHandle(handler ->
                handler.createQuery(this.getFiltersByLeaderboardId)
                        .bind(LEADERBOARD_ID, leaderboard.getRepositoryId())
                        .scanResultSet(this.filterMapper)
        );
    }

    @Override
    public List<Filter<P>> getFilters(final P player, final Leaderboard leaderboard) {
        return this.getDatabase().withHandle(handler ->
                handler.createQuery(this.getFiltersByPlayerLeaderboard)
                        .bind(PLAYER_ID, player.getRepositoryId())
                        .bind(LEADERBOARD_ID, leaderboard.getRepositoryId())
                        .scanResultSet(this.filterMapper)
        );
    }

    @Override
    public List<Filter<P>> getFilters(final P player,
                                      final Leaderboard leaderboard,
                                      final ZonedDateTime timestamp) {
        return this.getDatabase().withHandle(handler ->
                handler.createQuery(this.getFiltersByPlayerLeaderboardTime)
                        .bind(PLAYER_ID, player.getRepositoryId())
                        .bind(LEADERBOARD_ID, leaderboard.getRepositoryId())
                        .bind("timestamp", timestamp)
                        .scanResultSet(this.filterMapper)
        );
    }

    @Override
    public Filter<P> addFilter(final P player,
                               final Leaderboard leaderboard,
                               final Reason reason,
                               final ZonedDateTime filterStart,
                               final ZonedDateTime filterEnd) {
        return this.getDatabase().withHandle(handler ->
                handler.createQuery(this.insertFilter)
                        .bind(PLAYER_ID, player.getRepositoryId())
                        .bind(LEADERBOARD_ID, leaderboard.getRepositoryId())
                        .bind("filterReason", reason)
                        .bind("filterStart", filterStart)
                        .bind("filterEnd", filterEnd)
                        .scanResultSet(this.filterMapper)
                        .get(0)
        );
    }

    @Override
    public void removeFilter(final Filter<P> filter) {
        this.getDatabase().useHandle(handler ->
                handler.createUpdate(this.removeFilter)
                        .bind("repositoryId", filter.getRepositoryId())
                        .execute()
        );
    }

    private static class QueryTemplates {
        private static final String ADD_FILTER_REASON = "INSERT INTO $schema$.filter_reasons(reason) " +
                "SELECT :reason " +
                "WHERE " +
                "    NOT EXISTS ( " +
                "        SELECT id FROM $schema$.filter_reasons WHERE reason = :reason " +
                "    );";

        private static final String INSERT_FILTER = "INSERT INTO $schema$.filters(player_id, leaderboard_id, reason_id, filter_start, filter_end) " +
                "VALUES(:playerId, :leaderboardId, (SELECT id FROM $schema$.filter_reasons WHERE reason = :filterReason), :filterStart, :filterEnd) " +
                "RETURNING id, player_id, leaderboard_id, :filterReason reason, filter_start, filter_end";
        private static final String REMOVE_FILTER = "DELETE FROM $schema$.filters WHERE id = :repositoryId;";

        private static final String GET_FILTER_BASE = "SELECT filters.id, player_id, leaderboard_id, reason.reason, filter_start, filter_end " +
                "FROM $schema$.filters " +
                "INNER JOIN $schema$.filter_reasons reason ON reason.id = filters.reason_id " +
                "%s;";
        private static final String GET_FILTERS = String.format(GET_FILTER_BASE, "");
        private static final String GET_FILTERS_BY_PLAYER_ID = String.format(GET_FILTER_BASE, "WHERE player_id = :playerId");
        private static final String GET_FILTERS_BY_PLAYER_LEADERBOARD = String.format(GET_FILTER_BASE, "WHERE leaderboard_id = :leaderboardId AND player_id = :playerId;");
        private static final String GET_FILTERS_BY_PLAYER_LEADERBOARD_TIME = String.format(GET_FILTER_BASE, "WHERE player_id = :playerId AND leaderboard_id = :leaderboardId AND :timestamp BETWEEN filter_start AND filter_end;");
        private static final String GET_FILTERS_BY_LEADERBOARD_ID = String.format(GET_FILTER_BASE, "WHERE leaderboard_id = :leaderboardId");
    }
}
