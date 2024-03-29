package de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.postgres.mappers;

import de.timmi6790.mpstats.api.versions.v1.common.board.BoardService;
import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.LeaderboardRepository;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;

@AllArgsConstructor
public class LeaderboardMapper implements RowMapper<Leaderboard> {
    private final LeaderboardRepository leaderboardRepository;
    private final GameService gameService;
    private final StatService statService;
    private final BoardService boardService;

    @Override
    public Leaderboard map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        return new Leaderboard(
                this.leaderboardRepository,
                rs.getInt("id"),
                this.gameService.getGame(rs.getString("game_name")).orElseThrow(RuntimeException::new),
                this.statService.getStat(rs.getString("stat_name")).orElseThrow(RuntimeException::new),
                this.boardService.getBoard(rs.getString("board_name")).orElseThrow(RuntimeException::new),
                rs.getBoolean("deprecated"),
                rs.getTimestamp("last_update").toLocalDateTime().atZone(ZoneId.systemDefault()),
                rs.getTimestamp("last_cache_update").toLocalDateTime().atZone(ZoneId.systemDefault())
        );
    }
}
