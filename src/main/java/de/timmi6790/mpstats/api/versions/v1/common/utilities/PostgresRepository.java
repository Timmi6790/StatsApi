package de.timmi6790.mpstats.api.versions.v1.common.utilities;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jdbi.v3.core.Jdbi;

@AllArgsConstructor
@Getter(value = AccessLevel.PROTECTED)
public class PostgresRepository {
    private final Jdbi database;
    private final String schema;

    protected String formatQuery(final String query) {
        return query.replaceAll("\\$schema\\$", this.schema);
    }
}
