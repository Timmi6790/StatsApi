package de.timmi6790.mpstats.api.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;


@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(
                        new Info()
                                .version("4.0.0")
                                .title("StatsBot Stats API")
                                .description("The backend api behind the StatsBot")
                ).servers(new ArrayList<>());
    }
}
