package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.policy;

import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;

public interface Policy<P extends Player> {
    PolicyPriority getPriority();

    default void onPreLeaderboardFetch(final PreFetchPolicyEvent preFetchPolicyEvent) {
    }

    default void onCacheSave(final SavePolicyEvent<P> savePolicyEvent) {
    }


    default void onRepositorySave(final SavePolicyEvent<P> savePolicyEvent) {
    }
}
