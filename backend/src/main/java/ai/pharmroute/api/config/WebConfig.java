package ai.pharmroute.api.config;

import java.util.Arrays;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String[] allowedOrigins;

    public WebConfig(@Value("${pharmroute-ai.cors.allowed-origins}") String allowedOrigins) {
        this.allowedOrigins = Stream.concat(
                Stream.of("http://localhost:3000", "https://pharmatrace-web.onrender.com"),
                Arrays.stream(allowedOrigins.split(",")))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .distinct()
                .toArray(String[]::new);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET")
                .maxAge(3600);
    }
}
