package de.timmi6790.mpstats.api.versions.v1.common.filter.repository.postgres.mappers;

import com.google.common.collect.Lists;
import de.timmi6790.mpstats.api.versions.v1.common.filter.models.Reason;
import de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models.Filter;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.jdbi.v3.core.result.ResultSetScanner;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@AllArgsConstructor
@Log4j2
public class FilterMapper<P extends Player> implements ResultSetScanner<List<Filter<P>>> {
    private final PlayerService<P> playerService;
    private final LeaderboardService leaderboardService;

    @Override
    public List<Filter<P>> scanResultSet(final Supplier<ResultSet> resultSetSupplier, final StatementContext ctx) throws SQLException {
        try (ctx) {
            final ResultSet resultSet = resultSetSupplier.get();

            final List<Integer> playerIds = Lists.newArrayListWithCapacity(resultSet.getFetchSize());
            final List<Integer> leaderboardIds = Lists.newArrayListWithCapacity(playerIds.size());
            final List<PreParsedFilter> preParsedFilters = Lists.newArrayListWithCapacity(playerIds.size());
            while (resultSet.next()) {
                final int leaderboardId = resultSet.getInt("leaderboard_id");
                final int playerId = resultSet.getInt("player_id");
                final String reasonString = resultSet.getString("reason");

                Reason reason;
                try {
                    reason = Reason.valueOf(reasonString);
                } catch (final IllegalArgumentException e) {
                    log.warn("Invalid reason found of " + reasonString);
                    reason = Reason.GLITCHED;
                }

                playerIds.add(playerId);
                leaderboardIds.add(leaderboardId);
                preParsedFilters.add(
                        new PreParsedFilter(
                                playerId,
                                leaderboardId,
                                resultSet.getInt("id"),
                                reason,
                                resultSet.getTimestamp("filter_start").toLocalDateTime().atZone(ZoneId.systemDefault()),
                                resultSet.getTimestamp("filter_end").toLocalDateTime().atZone(ZoneId.systemDefault())
                        )
                );
            }

            final Map<Integer, P> players = this.playerService.getPlayers(playerIds);
            final Map<Integer, Leaderboard> leaderboards = this.leaderboardService.getLeaderboards(leaderboardIds);

            final List<Filter<P>> entries = Lists.newArrayListWithCapacity(preParsedFilters.size());
            for (final PreParsedFilter preValue : preParsedFilters) {
                final P player = players.get(preValue.getPlayerId());
                if (player == null) {
                    // This should never happen
                    log.warn("Can't find player with id " + preValue.getPlayerId());
                    continue;
                }

                final Leaderboard leaderboard = leaderboards.get(preValue.getLeaderboardId());
                if (leaderboard == null) {
                    // This should never happen
                    log.warn("Can't find leaderboard with id " + preValue.getLeaderboardId());
                    continue;
                }


                entries.add(
                        new Filter<>(
                                preValue.getFilterId(),
                                player,
                                leaderboard,
                                preValue.getReason(),
                                preValue.getStartDate(),
                                preValue.getEndDate()
                        )
                );
            }
            return entries;
        }
    }

    @Data
    private static class PreParsedFilter {
        private final int playerId;
        private final int leaderboardId;

        private final int filterId;
        private final Reason reason;
        private final ZonedDateTime startDate;
        private final ZonedDateTime endDate;
    }
}


