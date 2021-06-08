package de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.postgres;

import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.LeaderboardRepository;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.postgres.mappers.LeaderboardMapper;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import de.timmi6790.mpstats.api.versions.v1.common.utilities.PostgresRepository;
import org.jdbi.v3.core.Jdbi;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class LeaderboardPostgresRepository extends PostgresRepository implements LeaderboardRepository {
    private static final String GAME_ID = "gameId";
    private static final String STAT_ID = "statId";
    private static final String BOARD_ID = "boardId";
    private static final String REPOSITORY_ID = "repositoryId";

    private final String getLeaderboardsById;
    private final String getLeaderboard;
    private final String getLeaderboards;
    private final String getLeaderboardByRepositoryId;
    private final String getLeaderboardsByGameId;
    private final String getLeaderboardsByGameStatId;
    private final String getLeaderboardsByGameBoardId;
    private final String getLeaderboardsByStatId;
    private final String getLeaderboardsByStatBoardId;
    private final String getLeaderboardsByBoardId;

    private final String insertLeaderboard;

    private final String updateLeaderboardLastUpdate;
    private final String updateLeaderboardLastCacheUpdate;
    private final String updateLeaderboardDeprecated;

    private final LeaderboardMapper leaderboardMapper;

    public LeaderboardPostgresRepository(final Jdbi jdbi,
                                         final String schema,
                                         final GameService gameService,
                                         final StatService statService,
                                         final BoardService boardService) {
        super(jdbi, schema);

        this.leaderboardMapper = new LeaderboardMapper(this, gameService, statService, boardService);

        // Create queries
        this.getLeaderboardsById = this.formatQuery(QueryTemplates.GET_LEADERBOARDS_BY_ID);
        this.getLeaderboard = this.formatQuery(QueryTemplates.GET_LEADERBOARD);
        this.getLeaderboards = this.formatQuery(QueryTemplates.GET_LEADERBOARDS);
        this.getLeaderboardByRepositoryId = this.formatQuery(QueryTemplates.GET_LEADERBOARDS_BY_REPOSITORY_ID);
        this.getLeaderboardsByGameId = this.formatQuery(QueryTemplates.GET_LEADERBOARDS_BY_GAME_ID);
        this.getLeaderboardsByGameStatId = this.formatQuery(QueryTemplates.GET_LEADERBOARDS_BY_GAME_STAT_ID);
        this.getLeaderboardsByGameBoardId = this.formatQuery(QueryTemplates.GET_LEADERBOARDS_BY_GAME_BOARD_ID);
        this.getLeaderboardsByStatId = this.formatQuery(QueryTemplates.GET_LEADERBOARDS_BY_STAT_ID);
        this.getLeaderboardsByStatBoardId = this.formatQuery(QueryTemplates.GET_LEADERBOARDS_BY_STAT_BOARD_ID);
        this.getLeaderboardsByBoardId = this.formatQuery(QueryTemplates.GET_LEADERBOARDS_BY_BOARD_ID);

        this.insertLeaderboard = this.formatQuery(QueryTemplates.INSERT_LEADERBOARD);

        this.updateLeaderboardLastUpdate = this.formatQuery(QueryTemplates.UPDATE_LEADERBOARD_LAST_UPDATE);
        this.updateLeaderboardLastCacheUpdate = this.formatQuery(QueryTemplates.UPDATE_LEADERBOARD_LAST_CACHE_UPDATE);
        this.updateLeaderboardDeprecated = this.formatQuery(QueryTemplates.UPDATE_LEADERBOARD_DEPRECATED);
    }

    @Override
    public Map<Integer, Leaderboard> getLeaderboards(final Collection<Integer> repositoryIds) {
        if (repositoryIds.isEmpty()) {
            return new HashMap<>();
        }

        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getLeaderboardsById)
                        .bindList("repositoryIds", repositoryIds)
                        .map(this.leaderboardMapper)
                        .stream()
                        .collect(Collectors.toMap(Leaderboard::getRepositoryId, lb -> lb))
        );
    }

    @Override
    public List<Leaderboard> getLeaderboards() {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getLeaderboards)
                        .map(this.leaderboardMapper)
                        .list()
        );
    }

    @Override
    public List<Leaderboard> getLeaderboards(final Game game) {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getLeaderboardsByGameId)
                        .bind(GAME_ID, game.getRepositoryId())
                        .map(this.leaderboardMapper)
                        .list()
        );
    }

    @Override
    public List<Leaderboard> getLeaderboards(final Game game, final Stat stat) {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getLeaderboardsByGameStatId)
                        .bind(GAME_ID, game.getRepositoryId())
                        .bind(STAT_ID, stat.getRepositoryId())
                        .map(this.leaderboardMapper)
                        .list()
        );
    }

    @Override
    public List<Leaderboard> getLeaderboards(final Game game, final Board board) {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getLeaderboardsByGameBoardId)
                        .bind(GAME_ID, game.getRepositoryId())
                        .bind(BOARD_ID, board.getRepositoryId())
                        .map(this.leaderboardMapper)
                        .list()
        );
    }

    @Override
    public List<Leaderboard> getLeaderboards(final Stat stat) {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getLeaderboardsByStatId)
                        .bind(STAT_ID, stat.getRepositoryId())
                        .map(this.leaderboardMapper)
                        .list()
        );
    }

    @Override
    public List<Leaderboard> getLeaderboards(final Stat stat, final Board board) {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getLeaderboardsByStatBoardId)
                        .bind(STAT_ID, stat.getRepositoryId())
                        .bind(BOARD_ID, board.getRepositoryId())
                        .map(this.leaderboardMapper)
                        .list()
        );
    }

    @Override
    public List<Leaderboard> getLeaderboards(final Board board) {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getLeaderboardsByBoardId)
                        .bind(BOARD_ID, board.getRepositoryId())
                        .map(this.leaderboardMapper)
                        .list()
        );
    }

    @Override
    public Optional<Leaderboard> getLeaderboard(final int repositoryId) {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getLeaderboardByRepositoryId)
                        .bind(REPOSITORY_ID, repositoryId)
                        .map(this.leaderboardMapper)
                        .findFirst()
        );
    }

    @Override
    public Optional<Leaderboard> getLeaderboard(final Game game, final Stat stat, final Board board) {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getLeaderboard)
                        .bind(GAME_ID, game.getRepositoryId())
                        .bind(STAT_ID, stat.getRepositoryId())
                        .bind(BOARD_ID, board.getRepositoryId())
                        .map(this.leaderboardMapper)
                        .findFirst()
        );
    }

    @Override
    public Leaderboard createdLeaderboard(final Game game, final Stat stat, final Board board, final boolean deprecated) {
        this.getDatabase().useHandle(handle ->
                handle.createUpdate(this.insertLeaderboard)
                        .bind(GAME_ID, game.getRepositoryId())
                        .bind(STAT_ID, stat.getRepositoryId())
                        .bind(BOARD_ID, board.getRepositoryId())
                        .bind("deprecated", deprecated)
                        .execute()
        );

        return this.getLeaderboard(game, stat, board).orElseThrow(RuntimeException::new);
    }

    @Override
    public void setLeaderboardDeprecated(final int leaderboardId, final boolean deprecated) {
        this.getDatabase().useHandle(handle ->
                handle.createUpdate(this.updateLeaderboardDeprecated)
                        .bind(REPOSITORY_ID, leaderboardId)
                        .bind("deprecated", deprecated)
                        .execute()
        );
    }

    @Override
    public void setLeaderboardLastUpdate(final int leaderboardId, final ZonedDateTime lastUpdate) {
        this.getDatabase().useHandle(handle ->
                handle.createUpdate(this.updateLeaderboardLastUpdate)
                        .bind(REPOSITORY_ID, leaderboardId)
                        .bind("lastUpdate", lastUpdate)
                        .execute()
        );
    }

    @Override
    public void setLeaderboardLastCacheUpdate(final int leaderboardId, final ZonedDateTime lastUpdate) {
        this.getDatabase().useHandle(handle ->
                handle.createUpdate(this.updateLeaderboardLastCacheUpdate)
                        .bind(REPOSITORY_ID, leaderboardId)
                        .bind("lastUpdate", lastUpdate)
                        .execute()
        );
    }

    private static class QueryTemplates {
        private static final String GET_LEADERBOARD_BASE = "SELECT leaderboard.\"id\", game.game_name game_name, stat.stat_name stat_name, board.board_name board_name, leaderboard.deprecated, leaderboard.last_update, leaderboard.last_cache_update " +
                "FROM $schema$.leaderboards leaderboard " +
                "INNER JOIN $schema$.games game ON game.\"id\" = leaderboard.game_id " +
                "INNER JOIN $schema$.stats stat ON stat.\"id\" = leaderboard.stat_id " +
                "INNER JOIN $schema$.boards board ON board.\"id\" = leaderboard.board_id " +
                "%s;";
        private static final String GET_LEADERBOARDS_BY_ID = String.format(GET_LEADERBOARD_BASE, "WHERE leaderboard.id IN (<repositoryIds>)");
        private static final String GET_LEADERBOARD = String.format(GET_LEADERBOARD_BASE, "WHERE game_id = :gameId AND stat_id = :statId AND board_id = :boardId LIMIT 1");
        private static final String GET_LEADERBOARDS = String.format(GET_LEADERBOARD_BASE, "");
        private static final String GET_LEADERBOARDS_BY_REPOSITORY_ID = String.format(GET_LEADERBOARD_BASE, "WHERE leaderboard.\"id\" = :repositoryId");
        private static final String GET_LEADERBOARDS_BY_GAME_ID = String.format(GET_LEADERBOARD_BASE, "WHERE game_id = :gameId");
        private static final String GET_LEADERBOARDS_BY_GAME_STAT_ID = String.format(GET_LEADERBOARD_BASE, "WHERE game_id = :gameId AND stat_id = :statId");
        private static final String GET_LEADERBOARDS_BY_GAME_BOARD_ID = String.format(GET_LEADERBOARD_BASE, "WHERE game_id = :gameId AND board_id = :boardId");
        private static final String GET_LEADERBOARDS_BY_STAT_ID = String.format(GET_LEADERBOARD_BASE, "WHERE stat_id = :statId");
        private static final String GET_LEADERBOARDS_BY_STAT_BOARD_ID = String.format(GET_LEADERBOARD_BASE, "WHERE stat_id = :statId AND board_id = :boardId");
        private static final String GET_LEADERBOARDS_BY_BOARD_ID = String.format(GET_LEADERBOARD_BASE, "WHERE board_id = :boardId");

        private static final String INSERT_LEADERBOARD = "INSERT INTO $schema$.leaderboards(game_id, stat_id, board_id, deprecated) VALUES(:gameId, :statId, :boardId, :deprecated);";

        private static final String UPDATE_LEADERBOARD_LAST_UPDATE = "UPDATE $schema$.leaderboards SET last_update = :lastUpdate WHERE id = :repositoryId;";
        private static final String UPDATE_LEADERBOARD_LAST_CACHE_UPDATE = "UPDATE $schema$.leaderboards SET last_cache_update = :lastUpdate WHERE id = :repositoryId;";
        private static final String UPDATE_LEADERBOARD_DEPRECATED = "UPDATE $schema$.leaderboards SET deprecated = :deprecated WHERE id = :repositoryId;";
    }
}
