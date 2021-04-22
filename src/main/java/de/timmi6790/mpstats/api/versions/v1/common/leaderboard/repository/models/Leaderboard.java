package de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;

import java.sql.Timestamp;

public record Leaderboard(@JsonIgnore int repositoryId,
                          Game game,
                          Stat stat,
                          Board board,
                          boolean deprecated,
                          Timestamp lastUpdate) {
}
