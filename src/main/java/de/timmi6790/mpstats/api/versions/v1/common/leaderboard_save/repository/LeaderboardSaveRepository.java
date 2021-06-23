package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.repository;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.models.PlayerData;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardSave;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LeaderboardSaveRepository<P extends Player> {
    void saveLeaderboard(Leaderboard leaderboard, List<PlayerData> entries, ZonedDateTime saveTime);

    List<ZonedDateTime> getLeaderboardSaveTimes(Leaderboard leaderboard);

    Optional<LeaderboardSave<P>> getLeaderboardEntries(Leaderboard leaderboard,
                                                       ZonedDateTime saveTime);

    Map<Leaderboard, LeaderboardSave<P>> getLeaderboardEntries(Collection<Leaderboard> leaderboards,
                                                               ZonedDateTime saveTime);
}
