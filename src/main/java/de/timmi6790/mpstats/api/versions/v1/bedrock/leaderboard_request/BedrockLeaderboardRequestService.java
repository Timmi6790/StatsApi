package de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard_request;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;
import de.timmi6790.mpstats.api.Config;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_request.LeaderboardRequestService;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BedrockLeaderboardRequestService extends LeaderboardRequestService<Player> {
    private static final Pattern LEADERBOARD_PATTERN = Pattern.compile("^<td>\\d*<\\/td><td>(.{1,33})<\\/td><td> ([\\d,]*)<\\/td>");

    @Autowired
    public BedrockLeaderboardRequestService(final Config config) {
        super(config.getLeaderboard().getBedrockUrl(), 100);
    }

    @Override
    protected Optional<LeaderboardEntry<Player>> parseRow(final String row) {
        final Matcher leaderboardMatcher = LEADERBOARD_PATTERN.matcher(row);
        if (leaderboardMatcher.find()) {
            return Optional.of(
                    new LeaderboardEntry<>(
                            new Player(leaderboardMatcher.group(1)),
                            Long.parseLong(leaderboardMatcher.group(2).replace(",", ""))
                    )
            );
        }
        return Optional.empty();
    }
}