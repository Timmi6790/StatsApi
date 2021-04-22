package de.timmi6790.mpstats.api.versions.v1.java.group;

import de.timmi6790.mpstats.api.versions.v1.common.group.GroupService;
import lombok.extern.log4j.Log4j2;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class JavaGroupService extends GroupService {
    @Autowired
    public JavaGroupService(final Jdbi database) {
        super(database, "java_group");
    }
}
