package de.timmi6790.mpstats.api.versions.v1.common.group.repository.postgres.reducers;

import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import de.timmi6790.mpstats.api.versions.v1.common.group.repository.models.Group;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.result.LinkedHashMapRowReducer;
import org.jdbi.v3.core.result.RowView;

import java.util.Map;

@RequiredArgsConstructor
public class GroupReducer implements LinkedHashMapRowReducer<Integer, Group> {
    private final GameService gameService;

    @Override
    public void accumulate(final Map<Integer, Group> container, final RowView rowView) {
        final Group group = container.computeIfAbsent(
                rowView.getColumn("group_id", Integer.class),
                id -> rowView.getRow(Group.class)
        );

        final String aliasName = rowView.getColumn("alias_name", String.class);
        if (aliasName != null) {
            group.getAliasNames().add(aliasName);
        }

        final Integer gameId = rowView.getColumn("game_id", Integer.class);
        if (gameId != null) {
            this.gameService.getGame(gameId).ifPresent(game -> group.getGames().add(game));
        }
    }
}
