package de.timmi6790.mpstats.api.configs;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "StatsBot Stats API", version = "4.0.0", description = "The backend api behind the StatsBot"))
@SecurityScheme(
        name = "ApiKey",
        description = "Put your api key here.",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "X-Api-Key",
        scheme = "X-Api-Key"
)
public class OpenApiConfig {
}
