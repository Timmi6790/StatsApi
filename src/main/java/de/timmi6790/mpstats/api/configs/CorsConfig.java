package de.timmi6790.mpstats.api.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        // https://www.baeldung.com/spring-cors
        // Only allow it for get methods. All put methods currently require api key,
        // that is not something I wanna allow cors for
        registry.addMapping("/v1/**").allowedMethods("GET");
    }
}
