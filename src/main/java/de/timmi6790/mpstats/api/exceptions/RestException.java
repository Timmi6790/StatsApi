package de.timmi6790.mpstats.api.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
// TODO: THIS IS NOT SHOWN IN SPRING DOCS
public class RestException extends Exception {
    @Serial
    private static final long serialVersionUID = -4369186830978982900L;

    private final String errorId;
    private final String detail;

    public Map<String, Object> getErrorAttributes() {
        final Map<String, Object> errorAttributes = new HashMap<>();

        errorAttributes.put("message", this.detail);
        errorAttributes.put("error", this.errorId);

        return errorAttributes;
    }
}
