package de.timmi6790.mpstats.api.versions.v1.common.player_stats.generator;

import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.player_stats.models.PlayerEntry;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;

public abstract class BaseStatGenerator implements StatGenerator {
    protected static final String EXP_EARNED_WEBSITE_STAT_NAME = "ExpEarned";
    protected static final String LOSSES_WEBSITE_STAT_NAME = "Losses";
    protected static final String WINS_WEBSITE_STAT_NAME = "Wins";
    protected static final String KILL_WEBSITE_STAT_NAME = "Kills";

    protected static final String BLOCK_HUNT_GAME_WEBSITE_NAME = "Block%20Hunt";
    protected static final String HUNTER_KILLER_WEBSITE_NAME = "HunterKiller";
    protected static final String HUNTER_WINS_WEBSITE_NAME = "HunterWins";

    protected Optional<Long> getGamesPlayed(final Game game, final Map<String, PlayerEntry> playerEntries) {
        final PlayerEntry lossesEntry = playerEntries.get(LOSSES_WEBSITE_STAT_NAME);
        if (!this.isPresent(lossesEntry)) {
            return Optional.empty();
        }

        return this.getWins(game, playerEntries)
                .map(wins -> wins + lossesEntry.getScore());
    }

    protected Optional<Long> getKills(final Game game, final Map<String, PlayerEntry> playerEntries) {
        final ScoreConsumer scoreConsumer = new ScoreConsumer();
        final PlayerEntry killEntry = playerEntries.get(KILL_WEBSITE_STAT_NAME);
        if (this.isPresent(killEntry)) {
            scoreConsumer.addScore(killEntry);
        }

        // Special handling for bh
        if (game.getWebsiteName().equals(BLOCK_HUNT_GAME_WEBSITE_NAME)) {
            final PlayerEntry hunterKilledEntry = playerEntries.get(HUNTER_KILLER_WEBSITE_NAME);
            if (this.isPresent(hunterKilledEntry)) {
                scoreConsumer.addScore(hunterKilledEntry);
            }
        }

        if (scoreConsumer.isChanged()) {
            return Optional.of(scoreConsumer.getScore());
        }
        return Optional.empty();
    }

    protected Optional<Long> getWins(final Game game, final Map<String, PlayerEntry> playerEntries) {
        final ScoreConsumer scoreConsumer = new ScoreConsumer();

        final PlayerEntry winsEntry = playerEntries.get(WINS_WEBSITE_STAT_NAME);
        if (this.isPresent(winsEntry)) {
            scoreConsumer.addScore(winsEntry);
        }

        // Special handling for bh
        if (game.getWebsiteName().equals(BLOCK_HUNT_GAME_WEBSITE_NAME)) {
            final PlayerEntry hunterWinsEntry = playerEntries.get(HUNTER_WINS_WEBSITE_NAME);
            if (this.isPresent(hunterWinsEntry)) {
                scoreConsumer.addScore(hunterWinsEntry);
            }
        }

        if (scoreConsumer.isChanged()) {
            return Optional.of(scoreConsumer.getScore());
        }
        return Optional.empty();
    }

    protected boolean isPresent(final PlayerEntry playerEntry) {
        return playerEntry != null && playerEntry.isPresent();
    }

    @Getter
    private static class ScoreConsumer {
        private boolean changed;
        private long score = 0;

        public void addScore(final PlayerEntry entry) {
            this.addScore(entry.getScore());
        }

        public void addScore(final long score) {
            this.changed = true;
            this.score += score;
        }
    }
}
