package de.timmi6790.mpstats.api.versions.v1.java.leaderboard.repository.postgres;

import de.timmi6790.mpstats.api.versions.v1.common.game.models.Game;
import de.timmi6790.mpstats.api.versions.v1.java.board.JavaBoardService;
import de.timmi6790.mpstats.api.versions.v1.java.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.java.game.JavaGameService;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.repository.JavaLeaderboardRepository;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.repository.postgres.mappers.LeaderboardMapper;
import de.timmi6790.mpstats.api.versions.v1.java.stat.JavaStatService;
import de.timmi6790.mpstats.api.versions.v1.java.stat.repository.models.Stat;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JavaLeaderboardPostgresRepository implements JavaLeaderboardRepository {
    private static final String GET_LEADERBOARD_BASE = "SELECT leaderboard.\"id\", game.game_name game_name, stat.stat_name stat_name, board.board_name board_name, leaderboard.deprecated, leaderboard.last_update  " +
            "FROM java.leaderboards leaderboard " +
            "INNER JOIN java.games game ON game.\"id\" = leaderboard.game_id " +
            "INNER JOIN java.stats stat ON stat.\"id\" = leaderboard.stat_id " +
            "INNER JOIN java.boards board ON board.\"id\" = leaderboard.board_id " +
            "%s;";
    private static final String GET_LEADERBOARD = String.format(GET_LEADERBOARD_BASE, "WHERE game_id = :gameId AND stat_id = :statId AND board_id = :boardId LIMIT 1");
    private static final String GET_LEADERBOARDS = String.format(GET_LEADERBOARD_BASE, "");
    private static final String GET_LEADERBOARDS_GAMES = String.format(GET_LEADERBOARD_BASE, "WHERE game_id = :gameId");

    private static final String INSERT_LEADERBOARD = "INSERT INTO java.leaderboards(game_id, stat_id, board_id, deprecated) VALUES(:gameId, :statId, :boardId, :deprecated);";

    private final Jdbi database;

    @Autowired
    public JavaLeaderboardPostgresRepository(final Jdbi jdbi,
                                             final JavaGameService javaGameService,
                                             final JavaStatService javaStatService,
                                             final JavaBoardService javaBoardService) {
        this.database = jdbi;

        this.database.registerRowMapper(
                new LeaderboardMapper(javaGameService, javaStatService, javaBoardService)
        );
    }

    @Override
    public List<Leaderboard> getLeaderboards() {
        return this.database.withHandle(handle ->
                handle.createQuery(GET_LEADERBOARDS)
                        .mapTo(Leaderboard.class)
                        .list()
        );
    }

    @Override
    public List<Leaderboard> getLeaderboards(final Game game) {
        return this.database.withHandle(handle ->
                handle.createQuery(GET_LEADERBOARDS_GAMES)
                        .bind("gameId", game.getRepositoryId())
                        .mapTo(Leaderboard.class)
                        .list()
        );
    }

    @Override
    public Optional<Leaderboard> getLeaderboard(final Game game, final Stat stat, final Board board) {
        return this.database.withHandle(handle ->
                handle.createQuery(GET_LEADERBOARD)
                        .bind("gameId", game.getRepositoryId())
                        .bind("statId", stat.getRepositoryId())
                        .bind("boardId", board.getRepositoryId())
                        .mapTo(Leaderboard.class)
                        .findFirst()
        );
    }

    @Override
    public Leaderboard createdLeaderboard(final Game game, final Stat stat, final Board board, final boolean deprecated) {
        this.database.useHandle(handle ->
                handle.createUpdate(INSERT_LEADERBOARD)
                        .bind("gameId", game.getRepositoryId())
                        .bind("statId", stat.getRepositoryId())
                        .bind("boardId", board.getRepositoryId())
                        .bind("deprecated", deprecated)
                        .execute()
        );

        return this.getLeaderboard(game, stat, board).orElseThrow(RuntimeException::new);
    }

    @Override
    public void setLeaderboardDeprecated(final int leaderboardId, final boolean deprecated) {

    }

    @Override
    public void setLeaderboardLastUpdate(final int leaderboardId, final long lastUpdate) {

    }
}
