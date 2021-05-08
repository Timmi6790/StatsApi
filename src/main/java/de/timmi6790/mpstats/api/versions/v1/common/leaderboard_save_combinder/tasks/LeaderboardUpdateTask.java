package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.tasks;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.LeaderboardService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_cache.LeaderboardCacheService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_request.LeaderboardRequestService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.LeaderboardSaveService;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.policy.Policy;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.policy.PreFetchPolicyEvent;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.policy.SavePolicyEvent;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.policy.policies.DeprecatedPolicy;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.policy.policies.DuplicationPolicy;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.policy.policies.TimePolicy;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardSave;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Log4j2
public class LeaderboardUpdateTask<P extends Player> {
    private final LeaderboardService leaderboardService;
    private final LeaderboardRequestService<P> leaderboardRequestService;
    private final LeaderboardCacheService<P> leaderboardCacheService;
    private final LeaderboardSaveService<P> leaderboardSaveService;

    private final List<Policy<P>> savePolicies = new ArrayList<>();

    public LeaderboardUpdateTask(final LeaderboardService leaderboardService,
                                 final LeaderboardRequestService<P> leaderboardRequestService,
                                 final LeaderboardCacheService<P> leaderboardCacheService,
                                 final LeaderboardSaveService<P> leaderboardSaveService) {
        this.leaderboardService = leaderboardService;
        this.leaderboardRequestService = leaderboardRequestService;
        this.leaderboardCacheService = leaderboardCacheService;
        this.leaderboardSaveService = leaderboardSaveService;

        this.addSavePolicies(
                new TimePolicy<>(),
                new DeprecatedPolicy<>(),
                new DuplicationPolicy<>(leaderboardSaveService)
        );
    }

    @SafeVarargs
    private void addSavePolicies(final Policy<P>... savePolicies) {
        this.savePolicies.addAll(Arrays.asList(savePolicies));
        this.savePolicies.sort(Comparator.comparingInt(e -> e.getPriority().ordinal()));
    }

    public void updateLeaderboards() {
        log.info("Update leaderboards");
        final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setPriority(Thread.MIN_PRIORITY)
                .setNameFormat("lb-update-%d")
                .build();
        final ExecutorService executorService = Executors.newScheduledThreadPool(10, threadFactory);
        for (final Leaderboard leaderboard : this.leaderboardService.getLeaderboards()) {
            executorService.submit(() -> {
                // Pre check
                // Check if we even want to fetch this lb
                final PreFetchPolicyEvent preCachePolicyEvent = new PreFetchPolicyEvent(leaderboard);
                for (final Policy<P> policy : this.savePolicies) {
                    policy.onPreLeaderboardFetch(preCachePolicyEvent);
                }
                if (!preCachePolicyEvent.isShouldFetch()) {
                    return;
                }

                // Fetch web leaderboard
                final Optional<LeaderboardSave<P>> webLeaderboardOpt = this.leaderboardRequestService.retrieveLeaderboard(
                        leaderboard.getGame().getWebsiteName(),
                        leaderboard.getStat().getWebsiteName(),
                        leaderboard.getBoard().getWebsiteName()
                );
                if (webLeaderboardOpt.isEmpty()) {
                    return;
                }
                final LeaderboardSave<P> webLeaderboard = webLeaderboardOpt.get();

                // Check where we want to save it
                // Cache
                final SavePolicyEvent<P> cacheSavePolicyEvent = new SavePolicyEvent<>(leaderboard, webLeaderboard);
                for (final Policy<P> policy : this.savePolicies) {
                    policy.onCacheSave(cacheSavePolicyEvent);
                }
                if (cacheSavePolicyEvent.isShouldSave()) {
                    this.leaderboardCacheService.saveLeaderboardEntryPosition(
                            leaderboard,
                            webLeaderboard.getEntries(),
                            webLeaderboard.getSaveTime()
                    );
                }

                // Repository
                final SavePolicyEvent<P> repositorySavePolicyEvent = new SavePolicyEvent<>(leaderboard, webLeaderboard);
                for (final Policy<P> policy : this.savePolicies) {
                    policy.onRepositorySave(repositorySavePolicyEvent);
                }
                if (repositorySavePolicyEvent.isShouldSave()) {
                    this.leaderboardSaveService.saveLeaderboardEntries(
                            leaderboard,
                            webLeaderboard.getEntries(),
                            webLeaderboard.getSaveTime()
                    );
                }
            });
        }
    }
}
