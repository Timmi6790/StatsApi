package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_request.models;


import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;

public record WebLeaderboard<P extends Player>(P player, long score) {
}
