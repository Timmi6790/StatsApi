package de.timmi6790.mpstats.api.versions.v1.common.filter.repository.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.timmi6790.mpstats.api.versions.v1.common.filter.models.Reason;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import de.timmi6790.mpstats.api.versions.v1.common.player.models.Player;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

@Data
public class Filter<P extends Player> {
    @JsonIgnore
    private final int repositoryId;
    private final P player;
    private final Leaderboard leaderboard;
    private final Reason reason;
    private final boolean permanent;
    /**
     * The Filter duration. Null if permanent
     */
    @Nullable
    private final FilterDuration filterDuration;
}
