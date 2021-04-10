package de.timmi6790.mpstats.api.versions.v1.java.leaderboard.repository.postgres.mappers;

import de.timmi6790.mpstats.api.versions.v1.java.board.JavaBoardService;
import de.timmi6790.mpstats.api.versions.v1.java.game.JavaGameService;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.java.stat.JavaStatService;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

@AllArgsConstructor
public class LeaderboardMapper implements RowMapper<Leaderboard> {
    private final JavaGameService javaGameService;
    private final JavaStatService javaStatService;
    private final JavaBoardService javaBoardService;

    @Override
    public Leaderboard map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        return new Leaderboard(
                rs.getInt("id"),
                this.javaGameService.getGame(rs.getString("game_name")).orElseThrow(RuntimeException::new),
                this.javaStatService.getStat(rs.getString("stat_name")).orElseThrow(RuntimeException::new),
                this.javaBoardService.getBoard(rs.getString("board_name")).orElseThrow(RuntimeException::new),
                rs.getBoolean("deprecated"),
                rs.getTimestamp("last_update")
        );
    }
}
