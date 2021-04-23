package de.timmi6790.mpstats.api.versions.v1.java.stat;

import de.timmi6790.mpstats.api.utilities.java.JavaServiceGenerator;
import de.timmi6790.mpstats.api.versions.v1.common.stat.AbstractStatServiceTest;

class JavaStatServiceTest extends AbstractStatServiceTest {
    public JavaStatServiceTest() {
        super(JavaServiceGenerator::generateStatService);
    }
}