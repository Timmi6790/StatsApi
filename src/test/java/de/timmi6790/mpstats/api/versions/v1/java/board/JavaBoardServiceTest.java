package de.timmi6790.mpstats.api.versions.v1.java.board;

import de.timmi6790.mpstats.api.utilities.java.JavaServiceGenerator;
import de.timmi6790.mpstats.api.versions.v1.common.board.AbstractBoardServiceTest;

class JavaBoardServiceTest extends AbstractBoardServiceTest {
    public JavaBoardServiceTest() {
        super(JavaServiceGenerator::generateBoardService);
    }
}