package de.timmi6790.mineplex_stats_api.versions.v1.java.repository;

import de.timmi6790.mineplex_stats_api.versions.v1.java.repository.models.GroupsModel;
import de.timmi6790.mineplex_stats_api.versions.v1.java.repository.models.PlayerStatsModule;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JavaPlayerRepository {
    Optional<PlayerStatsModule> getPlayerStats(@NonNull String playerName,
                                               @NonNull UUID playerUUID,
                                               @NonNull String game,
                                               @NonNull String board,
                                               @NonNull LocalDateTime time,
                                               boolean filter);

    List<GroupsModel> getGroups();
}
