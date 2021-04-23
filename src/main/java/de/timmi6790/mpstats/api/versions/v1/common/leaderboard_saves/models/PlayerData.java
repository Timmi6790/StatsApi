package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_saves.models;

import de.timmi6790.mpstats.api.versions.v1.common.player.models.RepositoryPlayer;

public record PlayerData(int playerRepositoryId, long score) implements RepositoryPlayer {
    @Override
    public int getRepositoryId() {
        return this.playerRepositoryId;
    }
}
