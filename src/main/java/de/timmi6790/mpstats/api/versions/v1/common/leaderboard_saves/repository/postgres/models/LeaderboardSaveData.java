package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_saves.repository.postgres.models;


import java.time.LocalDateTime;

public record LeaderboardSaveData(int saveId, LocalDateTime saveTime) {
}
