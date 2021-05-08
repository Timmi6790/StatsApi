package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.policy.policies;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.policy.Policy;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.policy.PolicyPriority;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.policy.SavePolicyEvent;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;

import java.time.LocalDate;

public class DeprecatedPolicy<P extends Player> implements Policy<P> {
    @Override
    public PolicyPriority getPriority() {
        return PolicyPriority.LOW;
    }

    @Override
    public void onCacheSave(final SavePolicyEvent<P> savePolicyEvent) {
        // Deprecated lbs should never be saved into the cache
        if (savePolicyEvent.getLeaderboard().isDeprecated()) {
            savePolicyEvent.setShouldSave(false);
        }
    }

    @Override
    public void onRepositorySave(final SavePolicyEvent<P> savePolicyEvent) {
        final Leaderboard leaderboard = savePolicyEvent.getLeaderboard();
        // Only save one copy of if the board is deprecated
        if (leaderboard.isDeprecated() && leaderboard.getLastSaveTime().toLocalDate().equals(LocalDate.EPOCH)) {
            savePolicyEvent.setShouldSave(true);
        }
    }
}