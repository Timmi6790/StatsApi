package de.timmi6790.mpstats.api.versions.v1.common.leaderboard.exceptions;

import de.timmi6790.mpstats.api.exceptions.RestException;
import de.timmi6790.mpstats.api.versions.v1.common.leaderboard.repository.models.Leaderboard;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@ResponseStatus(HttpStatus.NOT_FOUND)
public class InvalidLeaderboardCombinationRestException extends RestException {
    @Serial
    private static final long serialVersionUID = -5300394532114866463L;

    private final List<Leaderboard> suggestedLeaderboards;

    public InvalidLeaderboardCombinationRestException(final List<Leaderboard> suggestedLeaderboards) {
        super("leaderboard-1", "No leaderboard combination found");

        this.suggestedLeaderboards = suggestedLeaderboards;
    }

    @Override
    public Map<String, Object> getErrorAttributes() {
        final Map<String, Object> errorAttributes = super.getErrorAttributes();

        errorAttributes.put("suggestedLeaderboards", this.suggestedLeaderboards);

        return errorAttributes;
    }
}
