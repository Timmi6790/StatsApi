package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.repository.postgres.mappers;

import com.google.common.collect.Lists;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.result.ResultSetScanner;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@AllArgsConstructor
public class LeaderboardEntryMapper<P extends Player> implements ResultSetScanner<List<LeaderboardEntry<P>>> {
    private final PlayerService<P> playerService;

    @Override
    public List<LeaderboardEntry<P>> scanResultSet(final Supplier<ResultSet> resultSetSupplier, final StatementContext ctx) throws SQLException {
        try (ctx) {
            final ResultSet resultSet = resultSetSupplier.get();

            final Map<Integer, Long> preValues = new LinkedHashMap<>(resultSet.getFetchSize());
            while (resultSet.next()) {
                final int playerId = resultSet.getInt("player_id");
                final long score = resultSet.getLong("score");

                preValues.put(playerId, score);
            }

            final Map<Integer, P> players = this.playerService.getPlayers(preValues.keySet());

            final List<LeaderboardEntry<P>> entries = Lists.newArrayListWithCapacity(preValues.size());
            for (final Map.Entry<Integer, Long> preValue : preValues.entrySet()) {
                final P player = players.get(preValue.getKey());
                if (player != null) {
                    entries.add(
                            new LeaderboardEntry<>(
                                    player,
                                    preValue.getValue()
                            )
                    );
                }
            }

            return entries;
        }
    }
}
