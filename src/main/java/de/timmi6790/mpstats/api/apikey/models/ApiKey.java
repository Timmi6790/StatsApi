package de.timmi6790.mpstats.api.apikey.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ApiKey {
    private final String publicPart;
    private final String hashedPrivatePart;
    private final ApiKeyProperties keyInformation;
}
