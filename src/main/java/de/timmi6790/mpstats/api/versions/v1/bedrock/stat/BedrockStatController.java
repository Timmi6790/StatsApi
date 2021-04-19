package de.timmi6790.mpstats.api.versions.v1.bedrock.stat;

import de.timmi6790.mpstats.api.versions.v1.common.stat.StatController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/bedrock/stat")
@Tag(name = "Bedrock - Stat")
public class BedrockStatController extends StatController {
    @Autowired
    public BedrockStatController(final BedrockStatService statService) {
        super(statService);
    }
}
