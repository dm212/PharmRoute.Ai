package ai.pharmroute.api.seed;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SeedDataLoader implements ApplicationRunner {

    private final Driver driver;
    private final Path scriptsDirectory;
    private final boolean enabled;

    public SeedDataLoader(
            Driver driver,
            @Value("${pharmroute-ai.seed.scripts-directory}") String scriptsDirectory,
            @Value("${pharmroute-ai.seed.enabled:false}") boolean enabled) {
        this.driver = driver;
        this.scriptsDirectory = Path.of(scriptsDirectory).toAbsolutePath().normalize();
        this.enabled = enabled;
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        System.out.println("PharmRoute.Ai seed loader enabled: " + enabled);
        if (!enabled) {
            return;
        }
        try (Session session = driver.session()) {
            executeScript(session, scriptsDirectory.resolve("constraints.cypher"));
            executeScript(session, scriptsDirectory.resolve("seed.cypher"));
        }
        System.out.println("PharmRoute.Ai seed data loaded successfully.");
    }

    private void executeScript(Session session, Path scriptPath) throws IOException {
        String script = removeCommentLines(Files.readString(scriptPath));
        List<String> statements = Arrays.stream(script.split(";"))
                .map(String::trim)
                .filter(statement -> !statement.isBlank())
                .toList();

        for (String statement : statements) {
            session.executeWrite(transaction -> {
                transaction.run(statement).consume();
                return null;
            });
        }
    }

    private String removeCommentLines(String statement) {
        return statement.lines()
                .filter(line -> !line.stripLeading().startsWith("//"))
                .reduce("", (left, right) -> left + System.lineSeparator() + right);
    }
}
