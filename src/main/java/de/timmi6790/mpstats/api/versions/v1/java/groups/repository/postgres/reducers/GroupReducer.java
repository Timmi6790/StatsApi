package de.timmi6790.mpstats.api.versions.v1.java.groups.repository.postgres.reducers;

import de.timmi6790.mpstats.api.versions.v1.java.groups.repository.models.Group;
import org.jdbi.v3.core.result.LinkedHashMapRowReducer;
import org.jdbi.v3.core.result.RowView;

import java.util.Map;

public class GroupReducer implements LinkedHashMapRowReducer<Integer, Group> {
    @Override
    public void accumulate(final Map<Integer, Group> container, final RowView rowView) {
        final Group group = container.computeIfAbsent(
                rowView.getColumn("group_id", Integer.class),
                id -> rowView.getRow(Group.class)
        );

        /*
        if (rowView.getColumn("pl_player_id", Integer.class) != null) {
            group.addGameRepositoryOnly(rowView.getRow(Game.class));
        }

         */
    }
}
