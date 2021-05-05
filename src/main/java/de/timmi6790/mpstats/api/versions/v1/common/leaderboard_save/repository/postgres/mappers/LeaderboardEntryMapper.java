package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.repository.postgres.mappers;

import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class LeaderboardEntryMapper<P extends Player> implements RowMapper<LeaderboardEntry<P>> {
    private final PlayerService<P> playerService;

    @Override
    public LeaderboardEntry<P> map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        final int playerId = rs.getInt("player_id");
        final Optional<P> playerOpt = this.playerService.getPlayer(playerId);
        if (playerOpt.isEmpty()) {
            // TODO: Log error
            return null;
        }

        return new LeaderboardEntry<>(
                playerOpt.get(),
                rs.getLong("score")
        );
    }
}
