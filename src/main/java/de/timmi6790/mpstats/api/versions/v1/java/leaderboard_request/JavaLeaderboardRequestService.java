package de.timmi6790.mpstats.api.versions.v1.java.leaderboard_request;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;
import de.timmi6790.mpstats.api.Config;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_request.AbstractLeaderboardRequest;
import de.timmi6790.mpstats.api.versions.v1.java.leaderboard_request.models.JavaWebLeaderboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class JavaLeaderboardRequestService extends AbstractLeaderboardRequest<JavaWebLeaderboard> {
    private static final Pattern LEADERBOARD_PATTERN = Pattern.compile("^<td>\\d*<\\/td>.*avatars\\/(.*)\\?size.*\\/players\\/(\\w{1,16}).*<td> ([\\d|,]*)<");

    @Autowired
    public JavaLeaderboardRequestService(final Config config) {
        super(config.getLeaderboard().getJavaUrl(), 1_000);
    }

    @Override
    protected Optional<JavaWebLeaderboard> parseRow(final String row) {
        final Matcher leaderboardMatcher = LEADERBOARD_PATTERN.matcher(row);
        if (leaderboardMatcher.find()) {
            return Optional.of(
                    new JavaWebLeaderboard(
                            leaderboardMatcher.group(2),
                            UUID.fromString(leaderboardMatcher.group(1)),
                            Long.parseLong(leaderboardMatcher.group(3).replace(",", ""))
                    )
            );
        }
        return Optional.empty();
    }
}