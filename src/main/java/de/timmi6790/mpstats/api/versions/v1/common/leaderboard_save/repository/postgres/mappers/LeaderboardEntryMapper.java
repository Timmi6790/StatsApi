package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.repository.postgres.mappers;

import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.result.ResultSetScanner;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Supplier;

@AllArgsConstructor
public class LeaderboardEntryMapper<P extends Player> implements ResultSetScanner<Map<Integer, List<LeaderboardEntry<P>>>> {
    private final PlayerService<P> playerService;

    @Override
    public Map<Integer, List<LeaderboardEntry<P>>> scanResultSet(final Supplier<ResultSet> resultSetSupplier, final StatementContext ctx) throws SQLException {
        try (ctx) {
            final ResultSet resultSet = resultSetSupplier.get();

            // Pre parse values
            final Set<Integer> playerIds = new HashSet<>();
            final Map<Integer, Map<Integer, Long>> preValues = new HashMap<>();
            while (resultSet.next()) {
                final int playerId = resultSet.getInt("player_id");

                final int leaderboardId = resultSet.getInt("leaderboard_id");
                final long score = resultSet.getLong("score");

                playerIds.add(playerId);
                preValues.computeIfAbsent(leaderboardId, k -> new LinkedHashMap<>()).put(playerId, score);
            }

            // Get players
            final Map<Integer, P> players = this.playerService.getPlayers(playerIds);

            // Map entries to final value
            final Map<Integer, List<LeaderboardEntry<P>>> parsedValues = new HashMap<>();
            for (final Map.Entry<Integer, Map<Integer, Long>> values : preValues.entrySet()) {
                final int leaderboardId = values.getKey();

                final List<LeaderboardEntry<P>> entries = new ArrayList<>();
                for (final Map.Entry<Integer, Long> playerEntry : values.getValue().entrySet()) {
                    final P player = players.get(playerEntry.getKey());
                    if (player != null) {
                        entries.add(
                                new LeaderboardEntry<>(
                                        player,
                                        playerEntry.getValue()
                                )
                        );
                    }
                }

                parsedValues.put(
                        leaderboardId,
                        entries
                );
            }

            return parsedValues;
        }
    }
}
