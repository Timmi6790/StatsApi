package de.timmi6790.mpstats.api.versions.v1.java.leaderboard_request.models;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_request.models.WebLeaderboard;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Getter
@ToString(callSuper = true)
public class JavaWebLeaderboard extends WebLeaderboard {
    private final UUID playerUUID;

    public JavaWebLeaderboard(final String player, final UUID playerUUID, final long score) {
        super(player, score);

        this.playerUUID = playerUUID;
    }
}
