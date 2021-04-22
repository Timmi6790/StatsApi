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

import java.util.List;
import java.util.Optional;

public class LeaderboardPostgresRepository extends PostgresRepository implements LeaderboardRepository {
    private static final String GAME_ID = "gameId";

    private final String getLeaderboard;
    private final String getLeaderboards;
    private final String getLeaderboardByRepositoryId;
    private final String getLeaderboardsByGameId;

    private final String insertLeaderboard;

    public LeaderboardPostgresRepository(final Jdbi jdbi,
                                         final String schema,
                                         final GameService gameService,
                                         final StatService statService,
                                         final BoardService boardService) {
        super(jdbi, schema);

        this.getDatabase()
                .registerRowMapper(
                        new LeaderboardMapper(gameService, statService, boardService)
                );

        // Create queries
        this.getLeaderboard = this.formatQuery(QueryTemplates.GET_LEADERBOARD);
        this.getLeaderboards = this.formatQuery(QueryTemplates.GET_LEADERBOARDS);
        this.getLeaderboardByRepositoryId = this.formatQuery(QueryTemplates.GET_LEADERBOARDS_BY_REPOSITORY_ID);
        this.getLeaderboardsByGameId = this.formatQuery(QueryTemplates.GET_LEADERBOARDS_BY_GAME_ID);
        this.insertLeaderboard = this.formatQuery(QueryTemplates.INSERT_LEADERBOARD);
    }

    @Override
    public List<Leaderboard> getLeaderboards() {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getLeaderboards)
                        .mapTo(Leaderboard.class)
                        .list()
        );
    }

    @Override
    public List<Leaderboard> getLeaderboards(final Game game) {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getLeaderboardsByGameId)
                        .bind(GAME_ID, game.repositoryId())
                        .mapTo(Leaderboard.class)
                        .list()
        );
    }

    @Override
    public Optional<Leaderboard> getLeaderboard(final int repositoryId) {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getLeaderboardByRepositoryId)
                        .bind("repositoryId", repositoryId)
                        .mapTo(Leaderboard.class)
                        .findFirst()
        );
    }

    @Override
    public Optional<Leaderboard> getLeaderboard(final Game game, final Stat stat, final Board board) {
        return this.getDatabase().withHandle(handle ->
                handle.createQuery(this.getLeaderboard)
                        .bind(GAME_ID, game.repositoryId())
                        .bind("statId", stat.repositoryId())
                        .bind("boardId", board.repositoryId())
                        .mapTo(Leaderboard.class)
                        .findFirst()
        );
    }

    @Override
    public Leaderboard createdLeaderboard(final Game game, final Stat stat, final Board board, final boolean deprecated) {
        this.getDatabase().useHandle(handle ->
                handle.createUpdate(this.insertLeaderboard)
                        .bind(GAME_ID, game.repositoryId())
                        .bind("statId", stat.repositoryId())
                        .bind("boardId", board.repositoryId())
                        .bind("deprecated", deprecated)
                        .execute()
        );

        return this.getLeaderboard(game, stat, board).orElseThrow(RuntimeException::new);
    }

    @Override
    public void setLeaderboardDeprecated(final int leaderboardId, final boolean deprecated) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLeaderboardLastUpdate(final int leaderboardId, final long lastUpdate) {
        throw new UnsupportedOperationException();
    }

    private static class QueryTemplates {
        private static final String GET_LEADERBOARD_BASE = "SELECT leaderboard.\"id\", game.game_name game_name, stat.stat_name stat_name, board.board_name board_name, leaderboard.deprecated, leaderboard.last_update  " +
                "FROM $schema$.leaderboards leaderboard " +
                "INNER JOIN $schema$.games game ON game.\"id\" = leaderboard.game_id " +
                "INNER JOIN $schema$.stats stat ON stat.\"id\" = leaderboard.stat_id " +
                "INNER JOIN $schema$.boards board ON board.\"id\" = leaderboard.board_id " +
                "%s;";
        private static final String GET_LEADERBOARD = String.format(GET_LEADERBOARD_BASE, "WHERE game_id = :gameId AND stat_id = :statId AND board_id = :boardId LIMIT 1");
        private static final String GET_LEADERBOARDS = String.format(GET_LEADERBOARD_BASE, "");
        private static final String GET_LEADERBOARDS_BY_REPOSITORY_ID = String.format(GET_LEADERBOARD_BASE, "WHERE leaderboard.\"id\" = :repositoryId");
        private static final String GET_LEADERBOARDS_BY_GAME_ID = String.format(GET_LEADERBOARD_BASE, "WHERE game_id = :gameId");

        private static final String INSERT_LEADERBOARD = "INSERT INTO $schema$.leaderboards(game_id, stat_id, board_id, deprecated) VALUES(:gameId, :statId, :boardId, :deprecated);";
    }
}
