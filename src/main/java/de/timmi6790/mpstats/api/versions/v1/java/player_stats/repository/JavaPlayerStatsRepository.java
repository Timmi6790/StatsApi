package de.timmi6790.mpstats.api.versions.v1.java.player_stats.repository;

import de.timmi6790.mpstats.api.versions.v1.java.player_stats.repository.models.PlayerStatsDatabaseModel;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface JavaPlayerStatsRepository {
    Optional<PlayerStatsDatabaseModel> getStats(@NonNull String playerName,
                                                @NonNull UUID playerUUID,
                                                @NonNull String game,
                                                @NonNull String board,
                                                @NonNull LocalDateTime time,
                                                boolean filter);
}
