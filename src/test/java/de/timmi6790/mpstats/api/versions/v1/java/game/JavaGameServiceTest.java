package de.timmi6790.mpstats.api.versions.v1.java.game;

import de.timmi6790.mpstats.api.utilities.java.JavaServiceGenerator;
import de.timmi6790.mpstats.api.versions.v1.common.game.AbstractGameServiceTest;


class JavaGameServiceTest extends AbstractGameServiceTest {
    public JavaGameServiceTest() {
        super(JavaServiceGenerator::generateGameService);
    }
}