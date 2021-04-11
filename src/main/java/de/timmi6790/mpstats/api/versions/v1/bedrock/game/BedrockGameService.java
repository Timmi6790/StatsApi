package de.timmi6790.mpstats.api.versions.v1.bedrock.game;

import de.timmi6790.mpstats.api.versions.v1.common.game.GameService;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BedrockGameService extends GameService {
    @Autowired
    public BedrockGameService(final Jdbi jdbi) {
        super(jdbi, "bedrock");
    }
}
