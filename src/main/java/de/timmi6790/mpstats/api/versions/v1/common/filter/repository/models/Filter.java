package de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.RepositoryPlayer;

import java.time.LocalDateTime;

public record Filter<P extends Player & RepositoryPlayer>(@JsonIgnore int repositoryId,
                                                          P player,
                                                          Leaderboard leaderboard,
                                                          String filterReason,
                                                          LocalDateTime filterStart,
                                                          LocalDateTime filterEnd) {
}
