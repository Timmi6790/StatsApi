package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.repository;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.models.PlayerData;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardSave;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LeaderboardSaveRepository<P extends Player> {
    void saveLeaderboard(Leaderboard leaderboard, List<PlayerData> entries, LocalDateTime saveTime);

    List<LocalDateTime> getLeaderboardSaveTimes(Leaderboard leaderboard);

    Optional<LeaderboardSave<P>> getLeaderboardEntries(Leaderboard leaderboard,
                                                       LocalDateTime saveTime);
}
