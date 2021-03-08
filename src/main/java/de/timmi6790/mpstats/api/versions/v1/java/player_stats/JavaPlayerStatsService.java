package de.timmi6790.mpstats.api.versions.v1.java.player_stats;

import de.timmi6790.mpstats.api.versions.v1.java.player_stats.models.PlayerStatsModel;
import de.timmi6790.mpstats.api.versions.v1.java.player_stats.repository.JavaPlayerStatsRepository;
import de.timmi6790.mpstats.api.versions.v1.java.player_stats.repository.models.PlayerStatsDatabaseModel;
import de.timmi6790.mpstats.api.versions.v1.website.WebsiteService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class JavaPlayerStatsService {
    final WebsiteService websiteService;
    private final JavaPlayerStatsRepository javaRepository;

    @Autowired
    public JavaPlayerStatsService(final WebsiteService websiteService, final JavaPlayerStatsRepository javaRepository) {
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
