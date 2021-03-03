package de.timmi6790.mineplex_stats_api.versions.v1.java.player;

import de.timmi6790.mineplex_stats_api.versions.v1.java.player.models.PlayerStatsModel;
import de.timmi6790.mineplex_stats_api.versions.v1.java.player.repository.JavaPlayerRepository;
import de.timmi6790.mineplex_stats_api.versions.v1.java.player.repository.models.PlayerStatsDatabaseModel;
import de.timmi6790.mineplex_stats_api.versions.v1.website.WebsiteService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class JavaPlayerService {
    final WebsiteService websiteService;
    private final JavaPlayerRepository javaRepository;

    @Autowired
    public JavaPlayerService(final WebsiteService websiteService, final JavaPlayerRepository javaRepository) {
        this.websiteService = websiteService;
        this.javaRepository = javaRepository;
    }

    public Optional<PlayerStatsModel> getPlayerStats(@NonNull final String playerName,
                                                     @NonNull final UUID playerUUID,
                                                     @NonNull final String game,
                                                     @NonNull final String board,
                                                     @NonNull final LocalDateTime time,
                                                     final boolean filter) {
        final Optional<PlayerStatsDatabaseModel> databaseModel = this.javaRepository.getStats(playerName, playerUUID, game, board, time, filter);
        return Optional.empty();
    }
}
