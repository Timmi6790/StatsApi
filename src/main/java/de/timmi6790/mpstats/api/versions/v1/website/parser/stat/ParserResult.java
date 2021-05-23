package de.timmi6790.mpstats.api.versions.v1.website.parser.stat;

import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import de.timmi6790.mpstats.api.versions.v1.website.models.GameStat;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ParserResult {
    private final Map<Game, GameStat> stats = new HashMap<>();

    private GameStat getGameStat(final Game game) {
        return this.stats.computeIfAbsent(game, k -> new GameStat());
    }

    public void add(final Game game, final String cleanStatName, final long value) {
        this.getGameStat(game).addStat(cleanStatName, value);
    }

    public void add(final Game game, final Stat stat, final long value) {
        this.getGameStat(game).addStat(stat, value);
    }
}
