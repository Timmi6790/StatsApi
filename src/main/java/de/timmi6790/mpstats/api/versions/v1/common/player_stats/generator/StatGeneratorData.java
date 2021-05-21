package de.timmi6790.mpstats.api.versions.v1.common.player_stats.generator;

import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.models.PlayerEntry;
import lombok.Data;

import java.util.*;
import java.util.function.Predicate;

@Data
public class StatGeneratorData {
    private final Set<PlayerEntry> playerStats;
    private Map<Game, Map<Board, Map<String, PlayerEntry>>> sortedStats;

    public Map<Game, Map<Board, Map<String, PlayerEntry>>> getSortedStats() {
        if (this.sortedStats == null) {
            this.sortedStats = new HashMap<>();
            for (final PlayerEntry playerEntry : this.playerStats) {
                final Leaderboard leaderboard = playerEntry.getLeaderboard();
                this.sortedStats.computeIfAbsent(leaderboard.getGame(), k -> new HashMap<>())
                        .computeIfAbsent(leaderboard.getBoard(), k -> new HashMap<>())
                        .put(playerEntry.getLeaderboard().getStat().getWebsiteName(), playerEntry);
            }
        }

        return this.sortedStats;
    }

    private List<PlayerEntry> filterEntries(final Collection<PlayerEntry> entries,
                                            final Predicate<PlayerEntry> playerEntryPredicate) {
        final List<PlayerEntry> filteredEntries = new ArrayList<>();
        for (final PlayerEntry entry : entries) {
            if (playerEntryPredicate.test(entry)) {
                filteredEntries.add(entry);
            }
        }
        return filteredEntries;
    }

    private Map<String, PlayerEntry> filterEntries(final Map<String, PlayerEntry> entries,
                                                   final Predicate<PlayerEntry> playerEntryPredicate) {
        final Map<String, PlayerEntry> filteredEntries = new HashMap<>();
        for (final Map.Entry<String, PlayerEntry> entry : entries.entrySet()) {
            if (playerEntryPredicate.test(entry.getValue())) {
                filteredEntries.put(entry.getKey(), entry.getValue());
            }
        }
        return filteredEntries;
    }

    public Optional<Map<Board, Map<String, PlayerEntry>>> getGameStats(final Game game) {
        return Optional.ofNullable(this.getSortedStats().get(game));
    }

    public List<Game> getGames() {
        return new ArrayList<>(this.getSortedStats().keySet());
    }

    public List<Board> getBoards(final Game game) {
        return this.getGameStats(game)
                .map(gameStats -> new ArrayList<>(gameStats.keySet()))
                .orElseGet(ArrayList::new);
    }

    public List<PlayerEntry> getPlayerEntries(final Predicate<PlayerEntry> playerEntryPredicate) {
        return this.filterEntries(
                this.playerStats,
                playerEntryPredicate
        );
    }

    public Map<String, PlayerEntry> getPlayerEntries(final Game game,
                                                     final Board board) {
        return this.getGameStats(game)
                .map(gameStats -> gameStats.getOrDefault(board, new HashMap<>()))
                .orElseGet(HashMap::new);
    }

    public Map<String, PlayerEntry> getPlayerEntries(final Game game,
                                                     final Board board,
                                                     final Predicate<PlayerEntry> playerEntryPredicate) {
        return this.filterEntries(
                this.getPlayerEntries(game, board),
                playerEntryPredicate
        );
    }
}
