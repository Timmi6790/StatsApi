package de.timmi6790.mpstats.api.versions.v1.java.group;

import de.timmi6790.mpstats.api.AbstractIntegrationTest;
import de.timmi6790.mpstats.api.utilities.java.JavaServiceGenerator;
import de.timmi6790.mpstats.api.versions.v1.common.group.AbstractGroupServiceTest;

class JavaGroupServiceTest extends AbstractGroupServiceTest {
    public JavaGroupServiceTest() {
        super(() -> new JavaGroupService(AbstractIntegrationTest.jdbi(), JavaServiceGenerator.generateGameService()));
    }
}