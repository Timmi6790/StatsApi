package de.timmi6790.mineplex_stats_api.versions.v1.java.player.repository;

import de.timmi6790.mineplex_stats_api.versions.v1.java.player.repository.models.PlayerStatsDatabaseModel;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface JavaPlayerRepository {
    Optional<PlayerStatsDatabaseModel> getStats(@NonNull String playerName,
                                                @NonNull UUID playerUUID,
                                                @NonNull String game,
                                                @NonNull String board,
                                                @NonNull LocalDateTime time,
                                                boolean filter);
}
