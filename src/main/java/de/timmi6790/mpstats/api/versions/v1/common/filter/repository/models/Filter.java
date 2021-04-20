package de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.RepositoryPlayer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Filter<PLAYER extends Player & RepositoryPlayer> {
    @JsonIgnore
    private final int repositoryId;
    private final PLAYER player;
    private final Leaderboard leaderboard;
    private final String filterReason;
    private final LocalDateTime filterStart;
    private final LocalDateTime filterEnd;
}
