package de.timmi6790.mpstats.api.versions.v1.common.stat.repository.postgres.reducers;

import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import org.jdbi.v3.core.result.LinkedHashMapRowReducer;
import org.jdbi.v3.core.result.RowView;

import java.util.Map;

public class StatReducer implements LinkedHashMapRowReducer<Integer, Stat> {
    @Override
    public void accumulate(final Map<Integer, Stat> container, final RowView rowView) {
        final Stat stat = container.computeIfAbsent(
                rowView.getColumn("stat_id", Integer.class),
                id -> rowView.getRow(Stat.class)
        );

        if (rowView.getColumn("alias_name", String.class) != null) {
            stat.getAliasNames().add(rowView.getColumn("alias_name", String.class));
        }
    }
}
