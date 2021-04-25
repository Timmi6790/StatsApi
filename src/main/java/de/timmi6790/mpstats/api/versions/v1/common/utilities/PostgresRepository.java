package de.timmi6790.mpstats.api.versions.v1.common.utilities;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jdbi.v3.core.Jdbi;

import java.util.regex.Pattern;

@AllArgsConstructor
@Getter(AccessLevel.PROTECTED)
public class PostgresRepository {
    private static final Pattern SCHEMA_PATTERN = Pattern.compile("\\$schema\\$");

    private final Jdbi database;
    private final String schema;

    protected String formatQuery(final String query) {
        return SCHEMA_PATTERN.matcher(query).replaceAll(this.schema);
    }
}
