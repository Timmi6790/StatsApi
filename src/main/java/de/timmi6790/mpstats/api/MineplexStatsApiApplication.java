package de.timmi6790.mpstats.api;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@Log4j2
public class MineplexStatsApiApplication {
    public static void main(final String[] args) {
        final SpringApplication app = new SpringApplication(MineplexStatsApiApplication.class);
        app.run(args);
    }
}
