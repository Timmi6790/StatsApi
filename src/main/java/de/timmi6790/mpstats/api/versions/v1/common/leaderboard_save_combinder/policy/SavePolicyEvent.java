package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.policy;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardSave;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.Data;

@Data
public class SavePolicyEvent<P extends Player> {
    private final Leaderboard leaderboard;
    private final LeaderboardSave<P> leaderboardSave;
    private boolean shouldSave = false;
}
