package de.timmi6790.mpstats.api.configs;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "StatsBot Stats API",
        version = "4.0.1",
        description = "The backend api behind the StatsBot. Everything in here can and will change. This api version is very unstable")
)
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
