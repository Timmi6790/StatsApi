package de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository;

import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LeaderboardRepository {
    Map<Integer, Leaderboard> getLeaderboards(final Collection<Integer> repositoryIds);

    List<Leaderboard> getLeaderboards();

    List<Leaderboard> getLeaderboards(Game game);

    List<Leaderboard> getLeaderboards(Game game, Stat stat);

    List<Leaderboard> getLeaderboards(Game game, Board board);

    List<Leaderboard> getLeaderboards(Stat stat);

    List<Leaderboard> getLeaderboards(Stat stat, Board board);

    List<Leaderboard> getLeaderboards(Board board);

    Optional<Leaderboard> getLeaderboard(int repositoryId);

    Optional<Leaderboard> getLeaderboard(Game game, Stat stat, Board board);

    Leaderboard createdLeaderboard(Game game, Stat stat, Board board, boolean deprecated);

    void setLeaderboardDeprecated(int leaderboardId, boolean deprecated);

    void setLeaderboardLastUpdate(int leaderboardId, ZonedDateTime lastUpdate);

    void setLeaderboardLastCacheUpdate(int leaderboardId, ZonedDateTime lastUpdate);
}
