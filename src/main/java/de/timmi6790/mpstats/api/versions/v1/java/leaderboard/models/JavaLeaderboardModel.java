package de.timmi6790.mpstats.api.versions.v1.java.leaderboard.models;

import de.timmi6790.mpstats.api.versions.v1.java.player.models.JavaPlayerModel;
import lombok.Data;

@Data
public class JavaLeaderboardModel {
    private final JavaPlayerModel player;
    private final int position;
    private final int score;
}
