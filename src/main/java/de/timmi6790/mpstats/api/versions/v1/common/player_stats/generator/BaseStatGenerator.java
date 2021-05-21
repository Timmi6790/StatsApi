package de.timmi6790.mpstats.api.versions.v1.common.player_stats.generator;

import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.models.PlayerEntry;

import java.util.Map;
import java.util.Optional;

public abstract class BaseStatGenerator implements StatGenerator {
    protected static final String EXP_EARNED_WEBSITE_STAT_NAME = "ExpEarned";
    protected static final String LOSSES_WEBSITE_STAT_NAME = "Losses";
    protected static final String WINS_WEBSITE_STAT_NAME = "Wins";
    protected static final String KILL_WEBSITE_STAT_NAME = "Kills";

    protected static final String BLOCK_HUNT_GAME_WEBSITE_NAME = "Block%20Hunt";
    protected static final String HUNTER_KILLER_WEBSITE_NAME = "HunterKiller";

    protected Optional<Long> getGamesPlayed(final Map<String, PlayerEntry> playerEntries) {
        final PlayerEntry lossesEntry = playerEntries.get(LOSSES_WEBSITE_STAT_NAME);
        if (!this.isPresent(lossesEntry)) {
            return Optional.empty();
        }

        final PlayerEntry winsEntry = playerEntries.get(WINS_WEBSITE_STAT_NAME);
        if (this.isPresent(winsEntry)) {
            return Optional.of(winsEntry.getScore() + lossesEntry.getScore());
        }

        return Optional.empty();
    }

    protected Optional<Long> getKills(final Game game, final Map<String, PlayerEntry> playerEntries) {
        long kills = 0;

        final PlayerEntry killEntry = playerEntries.get(KILL_WEBSITE_STAT_NAME);
        if (this.isPresent(killEntry)) {
            kills += killEntry.getScore();
        }

        if (game.getWebsiteName().equals(BLOCK_HUNT_GAME_WEBSITE_NAME)) {
            final PlayerEntry hunterKilledEntry = playerEntries.get(HUNTER_KILLER_WEBSITE_NAME);
            if (this.isPresent(hunterKilledEntry)) {
                kills += hunterKilledEntry.getScore();
            }
        }

        if (kills != 0) {
            return Optional.of(kills);
        }
        return Optional.empty();
    }

    protected boolean isPresent(final PlayerEntry playerEntry) {
        return playerEntry != null && playerEntry.isPresent();
    }
}
