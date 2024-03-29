package de.timmi6790.mpstats.api.versions.v1.java.stat;

import de.timmi6790.mpstats.api.versions.v1.common.stat.StatService;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JavaStatService extends StatService {
    @Autowired
    public JavaStatService(final Jdbi jdbi) {
        super(jdbi, "java");
    }
}
