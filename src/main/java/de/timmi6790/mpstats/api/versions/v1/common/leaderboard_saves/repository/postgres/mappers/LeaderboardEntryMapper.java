package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_saves.repository.postgres.mappers;

import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.RepositoryPlayer;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class LeaderboardEntryMapper<R extends Player & RepositoryPlayer> implements RowMapper<LeaderboardEntry<R>> {
    private final PlayerService<R> playerService;

    @Override
    public LeaderboardEntry<R> map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        final int playerId = rs.getInt("player_id");
        final Optional<R> playerOpt = this.playerService.getPlayer(playerId);
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
