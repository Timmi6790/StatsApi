package de.timmi6790.mpstats.api.versions.v1.bedrock.stat;

import de.timmi6790.mpstats.api.utilities.bedrock.BedrockServiceGenerator;
import de.timmi6790.mpstats.api.versions.v1.common.stat.AbstractStatServiceTest;

class BedrockStatServiceTest extends AbstractStatServiceTest {
    public BedrockStatServiceTest() {
        super(BedrockServiceGenerator::generateStatService);
    }
}