package de.timmi6790.mineplex_stats_api.versions.v1.bedrock.service;

import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BedrockService {
    private final Jdbi database;

    @Autowired
    public BedrockService(final Jdbi database) {
        this.database = database;
    }

}
