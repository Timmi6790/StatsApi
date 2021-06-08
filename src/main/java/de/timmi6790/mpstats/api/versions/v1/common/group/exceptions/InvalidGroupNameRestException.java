package de.timmi6790.mpstats.api.versions.v1.common.group.exceptions;

import de.timmi6790.mpstats.api.exceptions.RestException;
import de.timmi6790.mpstats.api.versions.v1.common.group.repository.models.Group;
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
public class InvalidGroupNameRestException extends RestException {
    @Serial
    private static final long serialVersionUID = 230808094405074156L;

    private final List<Group> suggestedGroups;

    public InvalidGroupNameRestException(final List<Group> suggestedGroups) {
        super("group-1", "Invalid group name");

        this.suggestedGroups = suggestedGroups;
    }

    @Override
    public Map<String, Object> getErrorAttributes() {
        final Map<String, Object> errorAttributes = super.getErrorAttributes();

        errorAttributes.put("suggestedGroups", this.suggestedGroups);

        return errorAttributes;
    }
}
