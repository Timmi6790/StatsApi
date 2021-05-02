package de.timmi6790.mpstats.api.versions.v1.java.stat;

import de.timmi6790.mpstats.api.versions.v1.common.stat.AbstractStatControllerTest;
import org.springframework.beans.factory.annotation.Autowired;

class JavaStatControllerTest extends AbstractStatControllerTest<JavaStatService> {
    @Autowired
    private JavaStatService statService;

    public JavaStatControllerTest() {
        super("/v1/java/stat");
    }

    @Override
    protected JavaStatService getStatService() {
        return this.statService;
    }
}