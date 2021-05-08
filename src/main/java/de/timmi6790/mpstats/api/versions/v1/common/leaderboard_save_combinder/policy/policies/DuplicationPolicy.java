package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.policy.policies;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.LeaderboardSaveService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.policy.Policy;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.policy.PolicyPriority;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.policy.SavePolicyEvent;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardSave;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.AllArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Assures that no duplicated leaderboards are saved into the repository
 *
 * @param <P> the player type
 */
@AllArgsConstructor
public class DuplicationPolicy<P extends Player> implements Policy<P> {
    private final LeaderboardSaveService<P> saveService;

    @Override
    public PolicyPriority getPriority() {
        return PolicyPriority.LOWEST;
    }

    @Override
    public void onRepositorySave(final SavePolicyEvent<P> savePolicyEvent) {
        if (!savePolicyEvent.isShouldSave()) {
            return;
        }

        final Leaderboard leaderboard = savePolicyEvent.getLeaderboard();
        final Optional<LeaderboardSave<P>> lastSaveOpt = this.saveService.retrieveLeaderboardSave(leaderboard, ZonedDateTime.now());
        if (lastSaveOpt.isEmpty()) {
            return;
        }

        final LeaderboardSave<P> lastSave = lastSaveOpt.get();
        final LeaderboardSave<P> newSave = savePolicyEvent.getLeaderboardSave();
        if (lastSave.equals(newSave)) {
            savePolicyEvent.setShouldSave(false);
        }
    }
}
