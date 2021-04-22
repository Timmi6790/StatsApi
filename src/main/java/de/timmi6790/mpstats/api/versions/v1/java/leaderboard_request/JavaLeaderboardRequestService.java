package de.timmi6790.mpstats.api.versions.v1.java.leaderboard_request;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;
import de.timmi6790.mpstats.api.Config;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_request.AbstractLeaderboardRequestService;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.java.player.repository.models.JavaPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class JavaLeaderboardRequestService extends AbstractLeaderboardRequestService<JavaPlayer> {
    private static final Pattern LEADERBOARD_PATTERN = Pattern.compile("^<td>\\d*<\\/td>.*avatars\\/(.*)\\?size.*\\/players\\/(\\w{1,16}).*<td> ([\\d|,]*)<");

    @Autowired
    public JavaLeaderboardRequestService(final Config config) {
        super(config.getLeaderboard().getJavaUrl(), 1_000);
    }

    @Override
    protected Optional<LeaderboardEntry<JavaPlayer>> parseRow(final String row) {
        final Matcher leaderboardMatcher = LEADERBOARD_PATTERN.matcher(row);
        if (leaderboardMatcher.find()) {
            return Optional.of(
                    new LeaderboardEntry<>(
                            new JavaPlayer(
                                    leaderboardMatcher.group(2),
                                    UUID.fromString(leaderboardMatcher.group(1))
                            ),
                            Long.parseLong(leaderboardMatcher.group(3).replace(",", ""))
                    )
            );
        }
        return Optional.empty();
    }
}
