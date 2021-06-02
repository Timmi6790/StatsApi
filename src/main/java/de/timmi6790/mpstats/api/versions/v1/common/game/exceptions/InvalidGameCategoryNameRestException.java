package de.timmi6790.mpstats.api.versions.v1.common.game.exceptions;

import de.timmi6790.mpstats.api.exceptions.RestException;
import de.timmi6790.mpstats.api.versions.v1.common.game.repository.models.GameCategory;
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
public class InvalidGameCategoryNameRestException extends RestException {
    @Serial
    private static final long serialVersionUID = -9211077185237597689L;

    private final List<GameCategory> suggestedGameCategories;

    public InvalidGameCategoryNameRestException(final List<GameCategory> suggestedGameCategories) {
        super("game-2", "Invalid game category name");

        this.suggestedGameCategories = suggestedGameCategories;
    }

    @Override
    public Map<String, Object> getErrorAttributes() {
        final Map<String, Object> errorAttributes = super.getErrorAttributes();

        errorAttributes.put("suggestedGameCategories", this.suggestedGameCategories);

        return errorAttributes;
    }
}
