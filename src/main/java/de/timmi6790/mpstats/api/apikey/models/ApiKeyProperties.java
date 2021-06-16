package de.timmi6790.mpstats.api.apikey.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ApiKeyProperties {
    private final RateLimit rateLimit;
    private final String[] authorities;

    public String[] getAuthorities() {
        if (this.authorities == null) {
            return new String[0];
        }
        return this.authorities;
    }
}
