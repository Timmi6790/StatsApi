package de.timmi6790.mpstats.api.versions.v1.common.game.repository.postgres.reducers;

import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.Game;
import org.jdbi.v3.core.result.LinkedHashMapRowReducer;
import org.jdbi.v3.core.result.RowView;

import java.util.Map;

public class GameReducer implements LinkedHashMapRowReducer<Integer, Game> {
    @Override
    public void accumulate(final Map<Integer, Game> container, final RowView rowView) {
        final Game game = container.computeIfAbsent(
                rowView.getColumn("game_id", Integer.class),
                id -> rowView.getRow(Game.class)
        );

        if (rowView.getColumn("alias_name", String.class) != null) {
            game.aliasNames().add(rowView.getColumn("alias_name", String.class));
        }
    }
}
