package de.timmi6790.mpstats.api.versions.v1.common.game.repository.models;

import edu.umd.cs.findbugs.annotations.Nullable;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Set;

@Data
@RedisHash("Game")
public class Game implements Serializable {
    private static final long serialVersionUID = -4902794194602567168L;
    @JsonIgnore
    @Id
    private final int repositoryId;
    @JsonIgnore
    private final String websiteName;

    private final String gameName;
    private final String cleanName;
    private final Set<String> aliasNames;
    private final String categoryName;
    @Nullable
    private final String description;
    @Nullable
    private final String wikiUrl;
}
