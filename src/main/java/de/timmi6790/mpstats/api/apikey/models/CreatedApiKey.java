package de.timmi6790.mpstats.api.apikey.models;

import lombok.Data;

@Data
public class CreatedApiKey {
    private final String apiKey;
    private final ApiKeyProperties keyInformation;
}
