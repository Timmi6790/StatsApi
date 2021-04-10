package de.timmi6790.mpstats.api.versions.v1.java.leaderboard.repository;

import de.timmi6790.mpstats.api.versions.v1.java.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.java.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.java.stat.repository.models.Stat;

import java.util.List;
import java.util.Optional;

public interface JavaLeaderboardRepository {
    List<Leaderboard> getLeaderboards();

    List<Leaderboard> getLeaderboards(Game game);

    Optional<Leaderboard> getLeaderboard(Game game, Stat stat, Board board);

    Leaderboard createdLeaderboard(Game game, Stat stat, Board board, boolean deprecated);

    void setLeaderboardDeprecated(int leaderboardId, boolean deprecated);

    void setLeaderboardLastUpdate(int leaderboardId, long lastUpdate);
}
