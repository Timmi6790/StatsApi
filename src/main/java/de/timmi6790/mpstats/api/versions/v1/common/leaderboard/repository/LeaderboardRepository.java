package de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository;

import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface LeaderboardRepository {
    List<Leaderboard> getLeaderboards();

    List<Leaderboard> getLeaderboards(Game game);

    List<Leaderboard> getLeaderboards(Game game, Board board);

    List<Leaderboard> getLeaderboards(Stat stat);

    List<Leaderboard> getLeaderboards(Stat stat, Board board);

    Optional<Leaderboard> getLeaderboard(int repositoryId);

    Optional<Leaderboard> getLeaderboard(Game game, Stat stat, Board board);

    Leaderboard createdLeaderboard(Game game, Stat stat, Board board, boolean deprecated);

    void setLeaderboardDeprecated(int leaderboardId, boolean deprecated);

    void setLeaderboardLastUpdate(int leaderboardId, ZonedDateTime lastUpdate);

    void setLeaderboardLastCacheUpdate(int leaderboardId, ZonedDateTime lastUpdate);
}
