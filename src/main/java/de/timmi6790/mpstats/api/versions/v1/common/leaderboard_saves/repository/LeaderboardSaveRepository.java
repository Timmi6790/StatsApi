package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_saves.repository;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_saves.models.PlayerData;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.RepositoryPlayer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LeaderboardSaveRepository<R extends Player & RepositoryPlayer> {
    void saveLeaderboard(Leaderboard leaderboard, List<PlayerData> entries, LocalDateTime saveTime);

    List<LocalDateTime> getLeaderboardSaveTimes(Leaderboard leaderboard);

    Optional<List<LeaderboardEntry<R>>> getLeaderboardEntries(Leaderboard leaderboard,
                                                              LocalDateTime saveTime);
}
