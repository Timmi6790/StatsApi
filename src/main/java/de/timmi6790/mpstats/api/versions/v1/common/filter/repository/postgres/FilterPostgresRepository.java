package de.timmi6790.mpstats.api.versions.v1.common.filter.repository.postgres;

import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.FilterRepository;
import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models.Filter;
import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.postgres.mappers.FilterMapper;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.RepositoryPlayer;
import de.timmi6790.mpstats.api.versions.v1.common.utilities.PostgresRepository;
import org.jdbi.v3.core.Jdbi;

import java.time.LocalDateTime;
import java.util.List;

public class FilterPostgresRepository<P extends Player & RepositoryPlayer> extends PostgresRepository implements FilterRepository<P> {
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
        this.insertFilter = this.formatQuery(QueryTemplates.INSERT_FILTER);
        this.removeFilter = this.formatQuery(QueryTemplates.REMOVE_FILTER);

        this.getFilters = this.formatQuery(QueryTemplates.GET_FILTERS);
        this.getFiltersByPlayerId = this.formatQuery(QueryTemplates.GET_FILTERS_BY_PLAYER_ID);
        this.getFiltersByLeaderboardId = this.formatQuery(QueryTemplates.GET_FILTERS_BY_LEADERBOARD_ID);
        this.getFiltersByPlayerLeaderboardTime = this.formatQuery(QueryTemplates.GET_FILTERS_BY_PLAYER_LEADERBOARD_TIME);
        this.getFiltersByPlayerLeaderboard = this.formatQuery(QueryTemplates.GET_FILTERS_BY_PLAYER_LEADERBOARD);
    }

    @Override
    public List<Filter<P>> getFilters() {
        return this.getDatabase().withHandle(handler ->
                handler.createQuery(this.getFilters)
                        .map(this.filterMapper)
                        .list()
        );
    }

    @Override
    public List<Filter<P>> getFilters(final P player) {
        return this.getDatabase().withHandle(handler ->
                handler.createQuery(this.getFiltersByPlayerId)
                        .bind("playerId", player.getRepositoryId())
                        .map(this.filterMapper)
                        .list()
        );
    }

    @Override
    public List<Filter<P>> getFilters(final Leaderboard leaderboard) {
        return this.getDatabase().withHandle(handler ->
                handler.createQuery(this.getFiltersByLeaderboardId)
                        .bind("leaderboardId", leaderboard.getRepositoryId())
                        .map(this.filterMapper)
                        .list()
        );
    }

    @Override
    public List<Filter<P>> getFilters(final P player, final Leaderboard leaderboard) {
        return this.getDatabase().withHandle(handler ->
                handler.createQuery(this.getFiltersByPlayerLeaderboard)
                        .bind("playerId", player.getRepositoryId())
                        .bind("leaderboardId", leaderboard.getRepositoryId())
                        .map(this.filterMapper)
                        .list()
        );
    }

    @Override
    public List<Filter<P>> getFilters(final P player,
                                      final Leaderboard leaderboard,
                                      final LocalDateTime timestamp) {
        return this.getDatabase().withHandle(handler ->
                handler.createQuery(this.getFiltersByPlayerLeaderboardTime)
                        .bind("playerId", player.getRepositoryId())
                        .bind("leaderboardId", leaderboard.getRepositoryId())
                        .bind("timestamp", timestamp)
                        .map(this.filterMapper)
                        .list()
        );
    }

    @Override
    public Filter<P> addFilter(final P player,
                               final Leaderboard leaderboard,
                               final String reason,
                               final LocalDateTime filterStart,
                               final LocalDateTime filterEnd) {
        return this.getDatabase().withHandle(handler ->
                handler.createQuery(this.insertFilter)
                        .bind("playerId", player.getRepositoryId())
                        .bind("leaderboardId", leaderboard.getRepositoryId())
                        .bind("filterReason", reason)
                        .bind("filterStart", filterStart)
                        .bind("filterEnd", filterEnd)
                        .map(this.filterMapper)
                        .first()
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
        private static final String INSERT_FILTER = "INSERT INTO $schema$.filters(player_id, leaderboard_id, filter_reason, filter_start, filter_end) VALUES(:playerId, :leaderboardId, :filterReason, :filterStart, :filterEnd) RETURNING id, player_id, leaderboard_id, filter_reason, filter_start, filter_end";
        private static final String REMOVE_FILTER = "DELETE FROM $schema$.filters WHERE id = :repositoryId;";

        private static final String GET_FILTER_BASE = "SELECT id, player_id, leaderboard_id, filter_reason, filter_start, filter_end FROM $schema$.filters %s;";
        private static final String GET_FILTERS = String.format(GET_FILTER_BASE, "");
        private static final String GET_FILTERS_BY_PLAYER_ID = String.format(GET_FILTER_BASE, "WHERE player_id = :playerId");
        private static final String GET_FILTERS_BY_PLAYER_LEADERBOARD = String.format(GET_FILTER_BASE, "WHERE leaderboard_id = :leaderboardId AND player_id = :playerId;");
        private static final String GET_FILTERS_BY_PLAYER_LEADERBOARD_TIME = String.format(GET_FILTER_BASE, "WHERE player_id = :playerId AND leaderboard_id = :leaderboardId AND :timestamp BETWEEN filter_start AND filter_end;");
        private static final String GET_FILTERS_BY_LEADERBOARD_ID = String.format(GET_FILTER_BASE, "WHERE leaderboard_id = :leaderboardId");
    }
}
