package de.timmi6790.mpstats.api.versions.v1.common.filter.repository.postgres.mappers;

import de.timmi6790.mpstats.api.versions.v1.common.filter.models.Reason;
import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models.Filter;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Optional;

@AllArgsConstructor
public class FilterMapper<P extends Player> implements RowMapper<Filter<P>> {
    private final PlayerService<P> playerService;
    private final LeaderboardService leaderboardService;

    @Override
    public Filter<P> map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        final int playerId = rs.getInt("player_id");
        final Optional<P> playerOpt = this.playerService.getPlayer(playerId);
        if (playerOpt.isEmpty()) {
            // TODO: Add warning
            return null;
        }

        final int leaderboardId = rs.getInt("leaderboard_id");
        final Optional<Leaderboard> leaderboardOpt = this.leaderboardService.getLeaderboard(leaderboardId);
        if (leaderboardOpt.isEmpty()) {
            // TODO: Add warning
            return null;
        }

        final Reason reason;
        try {
            reason = Reason.valueOf(rs.getString("reason"));
        } catch (final IllegalArgumentException e) {
            return null;
        }

        return new Filter<>(
                rs.getInt("id"),
                playerOpt.get(),
                leaderboardOpt.get(),
                reason,
                rs.getTimestamp("filter_start").toLocalDateTime().atZone(ZoneId.systemDefault()),
                rs.getTimestamp("filter_end").toLocalDateTime().atZone(ZoneId.systemDefault())
        );
    }
}
