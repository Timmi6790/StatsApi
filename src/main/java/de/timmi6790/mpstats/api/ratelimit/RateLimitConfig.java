package de.timmi6790.mpstats.api.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@Order(101)
public class RateLimitConfig extends WebSecurityConfigurerAdapter {
    private final RateLimitFilter rateLimitFilter;

    @Override
    protected void configure(final HttpSecurity http) {
        // Registering after the ApiKeyAuthenticationFilter is currently broken
        // https://github.com/spring-projects/spring-security/issues/9787
        http.addFilterAfter(
                this.rateLimitFilter,
                BasicAuthenticationFilter.class
        );
    }
}