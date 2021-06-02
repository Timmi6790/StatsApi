package de.timmi6790.mpstats.api.versions.v1.java.player_stats;

import de.timmi6790.mpstats.api.versions.v1.common.filter.models.Reason;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.PlayerStatsService;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.models.PlayerEntry;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard_save_combinder.JavaLeaderboardSaveCombinerService;
import de.timmi6790.mpstats.api.versions.v1.java.player.JavaPlayerService;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
import de.timmi6790.mpstats.api.versions.v1.website.WebsiteService;
import de.timmi6790.mpstats.api.versions.v1.website.models.GameStat;
import de.timmi6790.mpstats.api.versions.v1.website.models.WebsitePlayer;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class JavaPlayerStatsService extends PlayerStatsService<JavaPlayer, JavaPlayerService> {
    private static final String WEBSITE_BOARD_NAME = "All";

    private final WebsiteService websiteService;

    @Autowired
    public JavaPlayerStatsService(final JavaLeaderboardSaveCombinerService leaderboardSaveCombinerService,
                                  final WebsiteService websiteService) {
        super(leaderboardSaveCombinerService);

        this.websiteService = websiteService;
    }

    private Optional<PlayerEntry> getPlayerEntry(final Leaderboard leaderboard, final Set<PlayerEntry> entries) {
        for (final PlayerEntry entry : entries) {
            if (entry.getLeaderboard().equals(leaderboard)) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    private Optional<Long> getWebsiteScore(final Leaderboard leaderboard, final WebsitePlayer websitePlayer) {
        final GameStat gameStat = websitePlayer.getGameStats().get(leaderboard.getGame());
        if (gameStat != null) {
            return Optional.ofNullable(gameStat.getStats().get(leaderboard.getStat()));
        }
        return Optional.empty();
    }

    @SneakyThrows
    @Override
    protected Set<PlayerEntry> getPlayerEntries(final List<Leaderboard> leaderboards,
                                                final JavaPlayer player,
                                                final ZonedDateTime time,
                                                final Set<Reason> filterReasons,
                                                final boolean includeEmptyEntries) {
        final CompletableFuture<Optional<WebsitePlayer>> websitePlayerOpt = this.websiteService.retrievePlayer(player.getName());
        final Set<PlayerEntry> entries = super.getPlayerEntries(
                leaderboards,
                player,
                time,
                filterReasons,
                includeEmptyEntries
        );

        final Optional<WebsitePlayer> websitePlayer;
        try {
            websitePlayer = websitePlayerOpt.get(15, TimeUnit.SECONDS);
        } catch (final ExecutionException | TimeoutException e) {
            return entries;
        }

        if (websitePlayer.isPresent()) {
            final WebsitePlayer websiteStats = websitePlayer.get();
            for (final Leaderboard leaderboard : leaderboards) {
                // The website stats are only All time stats
                if (!WEBSITE_BOARD_NAME.equals(leaderboard.getBoard().getBoardName())) {
                    continue;
                }

                final Optional<Long> websiteScoreOpt = this.getWebsiteScore(leaderboard, websiteStats);
                if (websiteScoreOpt.isPresent()) {
                    // Remove an existing entry if it has no score
                    final Optional<PlayerEntry> entryOpt = this.getPlayerEntry(leaderboard, entries);
                    if (entryOpt.isPresent() && !entryOpt.get().isPresent()) {
                        entries.remove(entryOpt.get());
                    }

                    entries.add(
                            new PlayerEntry(
                                    leaderboard,
                                    ZonedDateTime.now(),
                                    websiteScoreOpt.get(),
                                    -1
                            )
                    );
                }
            }
        }

        return entries;
    }
}
