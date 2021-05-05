package de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Leaderboard {
    @JsonIgnore
    private final int repositoryId;
    private final Game game;
    private final Stat stat;
    private final Board board;
    private final boolean deprecated;
    private final LocalDateTime lastSave;
}
