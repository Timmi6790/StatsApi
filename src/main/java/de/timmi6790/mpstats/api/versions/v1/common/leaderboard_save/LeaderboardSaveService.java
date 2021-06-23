package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save;

import com.google.common.collect.Lists;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.models.PlayerData;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.repository.postgres.LeaderboardSavePostgresRepository;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardSave;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jdbi.v3.core.Jdbi;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Getter(AccessLevel.PROTECTED)
public abstract class LeaderboardSaveService<P extends Player> {
    private final PlayerService<P> playerService;
    private final LeaderboardSavePostgresRepository<P> repository;

    private final String schemaName;

    protected LeaderboardSaveService(final PlayerService<P> playerService, final Jdbi database, final String schema) {
        this.schemaName = schema;
        this.playerService = playerService;

        this.repository = new LeaderboardSavePostgresRepository<>(database, schema, playerService);
    }

    public List<ZonedDateTime> getLeaderboardSaveTimes(final Leaderboard leaderboard) {
        return this.repository.getLeaderboardSaveTimes(leaderboard);
    }

    public void saveLeaderboardEntries(final Leaderboard leaderboard,
                                       final List<LeaderboardEntry<P>> leaderboardData,
                                       final ZonedDateTime saveTime) {
        final List<PlayerData> parsedData = Lists.newArrayListWithCapacity(leaderboardData.size());
        for (final LeaderboardEntry<P> entry : leaderboardData) {
            parsedData.add(
                    new PlayerData(
                            entry.getPlayer().getRepositoryId(),
                            entry.getScore()
                    )
            );
        }

        if (!parsedData.isEmpty()) {
            log.info(
                    "[{}] Save {}-{}-{} into repository",
                    this.schemaName,
                    leaderboard.getGame().getGameName(),
                    leaderboard.getBoard().getBoardName(),
                    leaderboard.getStat().getStatName()
            );
            leaderboard.setLastSaveTime(saveTime);
            this.repository.saveLeaderboard(leaderboard, parsedData, saveTime);
        }
    }

    public Optional<LeaderboardSave<P>> retrieveLeaderboardSave(final Leaderboard leaderboard,
                                                                final ZonedDateTime saveTime) {
        return this.repository.getLeaderboardEntries(leaderboard, saveTime);
    }

    public Map<Leaderboard, LeaderboardSave<P>> retrieveLeaderboardSaves(final Collection<Leaderboard> leaderboards,
                                                                         final ZonedDateTime saveTime) {
        return this.repository.getLeaderboardEntries(leaderboards, saveTime);
    }
}
