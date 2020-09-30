package de.timmi6790.mineplex_stats_api.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {
    public static final String TAG_WEBSITE = "Website";

    public static final String TAG_JAVA = "Java";
    public static final String TAG_JAVA_INTERNAL = "Java - Internal";

    public static final String TAG_BEDROCK = "Bedrock";
    public static final String TAG_BEDROCK_INTERNAL = "Bedrock - Internal";

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .version("3.1.0")
                        .title("StatsBot Stats API")
                        .description("The backend api behind the StatsBot")
                );
    }
}
