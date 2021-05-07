package de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.LeaderboardRepository;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import lombok.*;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@EqualsAndHashCode(exclude = "leaderboardRepository")
@ToString(exclude = "leaderboardRepository")
public class Leaderboard {
    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    private final LeaderboardRepository leaderboardRepository;
    @JsonIgnore
    private final int repositoryId;
    private final Game game;
    private final Stat stat;
    private final Board board;
    private boolean deprecated;
    private ZonedDateTime lastSaveTime;
    private ZonedDateTime lastCacheSaveTime;

    public void setDeprecated(final boolean deprecated) {
        if (this.leaderboardRepository == null) {
            throw new UnsupportedOperationException();
        }

        if (this.deprecated != deprecated) {
            this.leaderboardRepository.setLeaderboardDeprecated(this.repositoryId, deprecated);
            this.deprecated = deprecated;
        }
    }

    public void setLastSaveTime(final ZonedDateTime lastSaveTime) {
        if (this.leaderboardRepository == null) {
            throw new UnsupportedOperationException();
        }

        if (this.lastSaveTime != lastSaveTime) {
            this.leaderboardRepository.setLeaderboardLastUpdate(this.repositoryId, lastSaveTime);
            this.lastSaveTime = lastSaveTime;
        }
    }

    public void setLastCacheSaveTime(final ZonedDateTime lastSave) {
        if (this.leaderboardRepository == null) {
            throw new UnsupportedOperationException();
        }

        if (this.lastCacheSaveTime != lastSave) {
            this.leaderboardRepository.setLeaderboardLastCacheUpdate(this.repositoryId, lastSave);
            this.lastCacheSaveTime = lastSave;
        }
    }
}
