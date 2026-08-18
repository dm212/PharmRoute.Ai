package ai.pharmroute.api.config;

import java.util.concurrent.TimeUnit;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DatabaseProperties.class)
public class GraphDatabaseConfig {

    @Bean(destroyMethod = "close")
    Driver graphDriver(DatabaseProperties properties) {
        Config driverConfig = Config.builder()
                .withConnectionTimeout(8, TimeUnit.SECONDS)
                .withMaxConnectionPoolSize(20)
                .withConnectionAcquisitionTimeout(8, TimeUnit.SECONDS)
                .build();

        return GraphDatabase.driver(
                properties.uri(),
                AuthTokens.basic(properties.username(), properties.password()),
                driverConfig);
    }
}
