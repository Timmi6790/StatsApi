package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.models;

import de.timmi6790.mpstats.api.versions.v1.common.player.models.RepositoryPlayer;

public record PlayerData(int playerRepositoryId, String playerName, long score) implements RepositoryPlayer {
    @Override
    public int getRepositoryId() {
        return this.playerRepositoryId;
    }

    @Override
    public String getPlayerName() {
        return this.playerName;
    }

    @Override
    public void setPlayerName(final String newPlayerName) {
        throw new UnsupportedOperationException();
    }
}
