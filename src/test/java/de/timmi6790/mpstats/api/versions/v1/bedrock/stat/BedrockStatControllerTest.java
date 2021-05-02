package de.timmi6790.mpstats.api.versions.v1.bedrock.stat;

import de.timmi6790.mpstats.api.versions.v1.common.stat.AbstractStatControllerTest;
import org.springframework.beans.factory.annotation.Autowired;

class BedrockStatControllerTest extends AbstractStatControllerTest<BedrockStatService> {
    @Autowired
    private BedrockStatService statService;

    public BedrockStatControllerTest() {
        super("/v1/bedrock/stat");
    }

    @Override
    protected BedrockStatService getStatService() {
        return this.statService;
    }
}