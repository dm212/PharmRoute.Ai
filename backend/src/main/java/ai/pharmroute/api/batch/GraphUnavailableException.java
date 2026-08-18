package ai.pharmroute.api.batch;

public class GraphUnavailableException extends RuntimeException {

    public GraphUnavailableException(Throwable cause) {
        super("The supply-chain graph is temporarily unavailable", cause);
    }
}
