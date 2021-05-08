package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.policy.policies;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.policy.Policy;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.policy.PolicyPriority;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.policy.PreFetchPolicyEvent;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save_combinder.policy.SavePolicyEvent;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.ValueRange;
import java.util.concurrent.TimeUnit;

public class TimePolicy<P extends Player> implements Policy<P> {
    private static final ZoneId MINEPLEX_ZONE = ZoneId.of("-6");

    @Override
    public PolicyPriority getPriority() {
        return PolicyPriority.HIGHEST;
    }

    @Override
    public void onPreLeaderboardFetch(final PreFetchPolicyEvent preFetchPolicyEvent) {
        final Leaderboard leaderboard = preFetchPolicyEvent.getLeaderboard();
        final long differenceSeconds = ChronoUnit.SECONDS.between(
                leaderboard.getLastCacheSaveTime(),
                ZonedDateTime.now()
        );
        if (differenceSeconds >= leaderboard.getBoard().getUpdateTime()) {
            preFetchPolicyEvent.setShouldFetch(true);
        }
    }

    @Override
    public void onCacheSave(final SavePolicyEvent<P> savePolicyEvent) {
        savePolicyEvent.setShouldSave(true);
    }

    @Override
    public void onRepositorySave(final SavePolicyEvent<P> savePolicyEvent) {
        if (savePolicyEvent.getLeaderboard().isDeprecated()) {
            return;
        }

        final Leaderboard leaderboard = savePolicyEvent.getLeaderboard();
        final ZonedDateTime lastSaveTime = leaderboard.getLastSaveTime().withZoneSameInstant(MINEPLEX_ZONE);
        final ZonedDateTime currentTime = ZonedDateTime.now(MINEPLEX_ZONE);
        final long saveIntervalHours = TimeUnit.SECONDS.toHours(leaderboard.getBoard().getUpdateTime()) + 1;
        final long saveAfterHour = 24 - saveIntervalHours;

        // Assure that we did not already make a copy today
        if (lastSaveTime.toLocalDate().isEqual(currentTime.toLocalDate())) {
            return;
        }

        // TODO: Find a better way for this. It feels wrong to do it based on those names
        switch (leaderboard.getBoard().getBoardName().toLowerCase()) {
            case "weekly":
                // Make one copy at the end of the week ~2 hours before the day ends.
                final DayOfWeek currentDay = DayOfWeek.from(LocalDate.now());
                if (currentDay == DayOfWeek.SUNDAY && currentTime.getHour() >= saveAfterHour) {
                    savePolicyEvent.setShouldSave(true);
                }
                break;
            case "all":
            case "daily":
                // Make one copy per day ~2 hours before the day ends.
                if (currentTime.getHour() >= saveAfterHour) {
                    savePolicyEvent.setShouldSave(true);
                }
                break;
            default:
                // Lets make default one copy at the end of the month
                final ValueRange range = currentTime.range(ChronoField.DAY_OF_MONTH);
                final long lastDayOfMonth = range.getMaximum();
                if (lastDayOfMonth == currentTime.getDayOfMonth() && currentTime.getHour() >= saveAfterHour) {
                    savePolicyEvent.setShouldSave(true);
                }
        }
    }
}
