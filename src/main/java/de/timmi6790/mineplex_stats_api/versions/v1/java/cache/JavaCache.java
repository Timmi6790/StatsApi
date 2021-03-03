package de.timmi6790.mineplex_stats_api.versions.v1.java.cache;

import de.timmi6790.mineplex_stats_api.versions.v1.java.leaderboard.models.JavaLeaderboardModel;

import java.util.List;

public interface JavaCache {
    void storeLeaderboard(int leaderboardId, boolean filtered, List<JavaLeaderboardModel> leaderboardModels);

    List<JavaLeaderboardModel> retrieveLeaderboard(int leaderboardId, boolean filtered);
}
