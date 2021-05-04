package de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save;

import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.models.PlayerData;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard_save.repository.postgres.LeaderboardSavePostgresRepository;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardEntry;
import de.timmi6790.mpstats.api.versions.v1.common.models.LeaderboardSave;
import de.timmi6790.mpstats.api.versions.v1.common.player.PlayerService;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.RepositoryPlayer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jdbi.v3.core.Jdbi;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Log4j2
public abstract class LeaderboardSaveService<P extends Player, R extends Player & RepositoryPlayer> {
    @Getter(AccessLevel.PROTECTED)
    private final PlayerService<R> playerService;
    @Getter(AccessLevel.PROTECTED)
    private final LeaderboardSavePostgresRepository<R> repository;

    private final String schemaName;

    protected LeaderboardSaveService(final PlayerService<R> playerService, final Jdbi database, final String schema) {
        this.schemaName = schema;
        this.playerService = playerService;

        this.repository = new LeaderboardSavePostgresRepository<>(database, schema, playerService);
    }

    protected abstract List<PlayerData> getPlayerData(final List<LeaderboardEntry<P>> leaderboardDataList);

    public List<LocalDateTime> getLeaderboardSaveTimes(final Leaderboard leaderboard) {
        return this.repository.getLeaderboardSaveTimes(leaderboard);
    }

    public void saveLeaderboardEntries(final Leaderboard leaderboard,
                                       final List<LeaderboardEntry<P>> leaderboardData,
                                       final LocalDateTime saveTime) {
        final List<PlayerData> parsedData = this.getPlayerData(leaderboardData);
        if (!parsedData.isEmpty()) {
            log.debug(
                    "[{}] Save {}-{}-{} into repository",
                    this.schemaName,
                    leaderboard.getGame().getGameName(),
                    leaderboard.getBoard().getBoardName(),
                    leaderboard.getStat().getStatName()
            );
            this.repository.saveLeaderboard(leaderboard, parsedData, saveTime);
        }
    }

    public Optional<LeaderboardSave<R>> retrieveLeaderboardSave(final Leaderboard leaderboard,
                                                                final LocalDateTime saveTime) {
        return this.repository.getLeaderboardEntries(leaderboard, saveTime);
    }
}
