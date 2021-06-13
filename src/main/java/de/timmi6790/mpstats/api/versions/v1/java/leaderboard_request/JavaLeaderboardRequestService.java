package de.timmi6790.mpstats.api.versions.v1.java.leaderboard_request;

import com.google.common.collect.Lists;
import com.google.re2j.Matcher;
import com.google.re2j.Pattern;
import de.timmi6790.mpstats.api.Config;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_request.LeaderboardRequestService;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.java.player.JavaPlayerService;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class JavaLeaderboardRequestService extends LeaderboardRequestService<JavaPlayer> {
    private static final Pattern LEADERBOARD_PATTERN = Pattern.compile("^<td>\\d*<\\/td>.*avatars\\/(.*)\\?size.*\\/players\\/(\\w{1,16}).*<td> ([\\d|,]*)<");

    private final JavaPlayerService playerService;

    @Autowired
    public JavaLeaderboardRequestService(final Config config,
                                         final JavaPlayerService playerService,
                                         final MeterRegistry meterRegistry) {
        super(config.getLeaderboard().getJavaUrl(), meterRegistry);

        this.playerService = playerService;
    }

    @Override
    protected List<LeaderboardEntry<JavaPlayer>> parseRows(final String[] rows) {
        final Map<UUID, String> parsedPlayers = new HashMap<>();
        final List<PreEntry> entries = Lists.newArrayListWithCapacity(1_000);
        for (final String row : rows) {
            final Matcher leaderboardMatcher = LEADERBOARD_PATTERN.matcher(row);
            if (leaderboardMatcher.find()) {
                final String playerName = leaderboardMatcher.group(2);
                final UUID playerUUID = UUID.fromString(leaderboardMatcher.group(1));
                final long score = Long.parseLong(leaderboardMatcher.group(3).replace(",", ""));

                parsedPlayers.put(playerUUID, playerName);
                entries.add(
                        new PreEntry(
                                playerUUID,
                                score
                        )
                );
            }
        }

        final Map<UUID, JavaPlayer> players = this.playerService.getPlayersOrCreate(parsedPlayers);
        final List<LeaderboardEntry<JavaPlayer>> parsedEntries = Lists.newArrayListWithCapacity(entries.size());
        for (final PreEntry entry : entries) {
            parsedEntries.add(
                    new LeaderboardEntry<>(
                            players.get(entry.getPlayerUUID()),
                            entry.getScore()
                    )
            );
        }

        return parsedEntries;
    }

    @Data
    private static class PreEntry {
        private final UUID playerUUID;
        private final long score;
    }
}
