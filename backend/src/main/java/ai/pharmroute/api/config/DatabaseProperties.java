package ai.pharmroute.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("pharmroute-ai.database")
public record DatabaseProperties(String uri, String username, String password) {
}
