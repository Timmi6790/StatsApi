package de.timmi6790.mpstats.api.versions.v1.bedrock.filter;

import de.timmi6790.mpstats.api.versions.v1.bedrock.filter.repository.BedrockFilterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BedrockFilterService {
    private final BedrockFilterRepository bedrockFilterRepository;

    @Autowired
    public BedrockFilterService(final BedrockFilterRepository bedrockFilterRepository) {
        this.bedrockFilterRepository = bedrockFilterRepository;
    }
}
