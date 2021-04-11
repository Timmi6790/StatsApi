package de.timmi6790.mpstats.api.versions.v1.bedrock.stat;

import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BedrockStatService extends StatService {
    @Autowired
    public BedrockStatService(final Jdbi jdbi) {
        super(jdbi, "bedrock");
    }
}
