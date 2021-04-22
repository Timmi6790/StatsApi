package de.timmi6790.mpstats.api.versions.v1.common.board.repository.postgres.reducers;

import de.timmi6790.mpstats.api.versions.v1.common.board.repository.models.Board;
import org.jdbi.v3.core.result.LinkedHashMapRowReducer;
import org.jdbi.v3.core.result.RowView;

import java.util.Map;

public class BoardReducer implements LinkedHashMapRowReducer<Integer, Board> {
    @Override
    public void accumulate(final Map<Integer, Board> container, final RowView rowView) {
        final Board board = container.computeIfAbsent(
                rowView.getColumn("board_id", Integer.class),
                id -> rowView.getRow(Board.class)
        );

        if (rowView.getColumn("alias_name", String.class) != null) {
            board.aliasNames().add(rowView.getColumn("alias_name", String.class));
        }
    }
}
