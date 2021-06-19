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
import io.sentry.Sentry;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Log4j2
public class LeaderboardUpdateTask<P extends Player> {
    private static final int UPDATE_POOL_SIZE = 15;

    private final LeaderboardService leaderboardService;
    private final LeaderboardRequestService<P> leaderboardRequestService;
    private final LeaderboardCacheService<P> leaderboardCacheService;
    private final LeaderboardSaveService<P> leaderboardSaveService;

    private final List<Policy<P>> savePolicies = new ArrayList<>();
    private final ExecutorService executorService;

    public LeaderboardUpdateTask(final LeaderboardService leaderboardService,
                                 final LeaderboardRequestService<P> leaderboardRequestService,
                                 final LeaderboardCacheService<P> leaderboardCacheService,
                                 final LeaderboardSaveService<P> leaderboardSaveService) {
        this.leaderboardService = leaderboardService;
        this.leaderboardRequestService = leaderboardRequestService;
        this.leaderboardCacheService = leaderboardCacheService;
        this.leaderboardSaveService = leaderboardSaveService;

        final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setPriority(Thread.MIN_PRIORITY)
                .setNameFormat("lb-update-%d")
                .build();

        this.executorService = Executors.newScheduledThreadPool(UPDATE_POOL_SIZE, threadFactory);

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

    private boolean shouldFetchLeaderboard(final Leaderboard leaderboard) {
        final PreFetchPolicyEvent preCachePolicyEvent = new PreFetchPolicyEvent(leaderboard);
        for (final Policy<P> policy : this.savePolicies) {
            policy.onPreLeaderboardFetch(preCachePolicyEvent);
        }
        return preCachePolicyEvent.isShouldFetch();
    }

    private boolean shouldSaveIntoCache(final Leaderboard leaderboard, final LeaderboardSave<P> webLeaderboard) {
        final SavePolicyEvent<P> cacheSavePolicyEvent = new SavePolicyEvent<>(leaderboard, webLeaderboard);
        for (final Policy<P> policy : this.savePolicies) {
            policy.onCacheSave(cacheSavePolicyEvent);
        }
        return cacheSavePolicyEvent.isShouldSave();
    }

    private boolean shouldSaveIntoRepository(final Leaderboard leaderboard, final LeaderboardSave<P> webLeaderboard) {
        final SavePolicyEvent<P> repositorySavePolicyEvent = new SavePolicyEvent<>(leaderboard, webLeaderboard);
        for (final Policy<P> policy : this.savePolicies) {
            policy.onRepositorySave(repositorySavePolicyEvent);
        }
        return repositorySavePolicyEvent.isShouldSave();
    }

    private String getLeaderboardLogName(final Leaderboard leaderboard) {
        return String.format(
                "%s-%s-%s",
                leaderboard.getGame().getWebsiteName(),
                leaderboard.getStat().getWebsiteName(),
                leaderboard.getBoard().getWebsiteName()
        );
    }

    public void updateLeaderboards() {
        log.info("Update leaderboards");
        for (final Leaderboard leaderboard : this.leaderboardService.getLeaderboards()) {
            this.executorService.submit(() -> {
                try {
                    // Pre check
                    // Check if we even want to fetch this lb
                    if (!this.shouldFetchLeaderboard(leaderboard)) {
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
                    if (this.shouldSaveIntoCache(leaderboard, webLeaderboard)) {
                        this.leaderboardCacheService.saveLeaderboardEntryPosition(
                                leaderboard,
                                webLeaderboard.getEntries(),
                                webLeaderboard.getSaveTime()
                        );
                    }

                    // Repository
                    if (this.shouldSaveIntoRepository(leaderboard, webLeaderboard)) {
                        this.leaderboardSaveService.saveLeaderboardEntries(
                                leaderboard,
                                webLeaderboard.getEntries(),
                                webLeaderboard.getSaveTime()
                        );
                    }
                } catch (final Exception e) {
                    log.error(
                            "Exception during " + this.getLeaderboardLogName(leaderboard),
                            e
                    );
                    Sentry.captureException(e);
                }
            });
        }
    }
}
