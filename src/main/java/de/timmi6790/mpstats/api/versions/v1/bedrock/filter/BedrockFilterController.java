package de.timmi6790.mpstats.api.versions.v1.bedrock.filter;

import de.timmi6790.mpstats.api.versions.v1.bedrock.player.BedrockPlayerService;
import de.timmi6790.mpstats.api.versions.v1.bedrock.player.repository.models.BedrockRepositoryPlayer;
import de.timmi6790.mpstats.api.versions.v1.common.filter.FilterController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/bedrock/filter")
@Tag(name = "Bedrock - Filter")
public class BedrockFilterController extends FilterController<BedrockRepositoryPlayer, BedrockPlayerService> {
    @Autowired
    public BedrockFilterController(final BedrockFilterService filterService) {
        super(filterService);
    }
}
