package de.timmi6790.mpstats.api.versions.v1.bedrock.leaderboard_request;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.re2j.Matcher;
import com.google.re2j.Pattern;
import de.timmi6790.commons.Pair;
import de.timmi6790.mpstats.api.Config;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.BedrockPlayerService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockPlayer;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_request.LeaderboardRequestService;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class BedrockLeaderboardRequestService extends LeaderboardRequestService<BedrockPlayer> {
    private static final Pattern LEADERBOARD_PATTERN = Pattern.compile("^<td>\\d*<\\/td><td>(.{1,33})<\\/td><td> ([\\d,]*)<\\/td>");

    private final BedrockPlayerService playerService;

    @Autowired
    public BedrockLeaderboardRequestService(final Config config, final BedrockPlayerService playerService) {
        super(config.getLeaderboard().getBedrockUrl());
        this.playerService = playerService;
    }

    @Override
    protected List<LeaderboardEntry<BedrockPlayer>> parseRows(final String[] rows) {
        final Set<String> parsedPlayers = Sets.newHashSetWithExpectedSize(100);
        final List<Pair<String, Long>> entries = Lists.newArrayListWithCapacity(100);
        for (final String row : rows) {
            final Matcher leaderboardMatcher = LEADERBOARD_PATTERN.matcher(row);
            if (leaderboardMatcher.find()) {
                final String playerName = leaderboardMatcher.group(1);
                final long score = Long.parseLong(leaderboardMatcher.group(2).replace(",", ""));

                if (parsedPlayers.add(playerName)) {
                    entries.add(
                            new Pair<>(
                                    playerName,
                                    score
                            )
                    );
                }
            }
        }

        final Map<String, BedrockPlayer> players = this.playerService.getPlayersOrCreate(parsedPlayers);
        final List<LeaderboardEntry<BedrockPlayer>> parsedEntries = Lists.newArrayListWithCapacity(entries.size());
        for (final Pair<String, Long> entry : entries) {
            parsedEntries.add(
                    new LeaderboardEntry<>(
                            players.get(entry.getLeft()),
                            entry.getRight()
                    )
            );
        }

        return parsedEntries;
    }
}