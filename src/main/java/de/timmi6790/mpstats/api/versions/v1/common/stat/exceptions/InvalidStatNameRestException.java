package de.timmi6790.mpstats.api.versions.v1.common.stat.exceptions;

import de.timmi6790.mpstats.api.exceptions.RestException;
import de.timmi6790.mpstats.api.versions.v1.common.stat.repository.models.Stat;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.List;
import java.util.Map;

@ResponseStatus(HttpStatus.NOT_FOUND)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InvalidStatNameRestException extends RestException {
    @Serial
    private static final long serialVersionUID = 7261872324684137651L;

    private final List<Stat> suggestedStats;

    public InvalidStatNameRestException(final List<Stat> suggestedStats) {
        super("stat-1", "Invalid stat name");

        this.suggestedStats = suggestedStats;
    }

    @Override
    public Map<String, Object> getErrorAttributes() {
        final Map<String, Object> errorAttributes = super.getErrorAttributes();

        errorAttributes.put("suggestedStats", this.suggestedStats);

        return errorAttributes;
    }
}
