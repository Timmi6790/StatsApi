package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.policy;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import lombok.Data;

@Data
public class PreFetchPolicyEvent {
    private final Leaderboard leaderboard;
    private boolean shouldFetch = false;
}
