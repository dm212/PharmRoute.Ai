package ai.pharmroute.api.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import ai.pharmroute.api.batch.BatchNotFoundException;
import ai.pharmroute.api.batch.GraphUnavailableException;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void mapsUnknownBatchToNotFoundResponse() {
        var response = handler.handleNotFound(new BatchNotFoundException("BT-404"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("BATCH_NOT_FOUND", response.getBody().code());
        assertEquals(404, response.getBody().status());
    }

    @Test
    void hidesDatabaseImplementationDetailsFromClients() {
        var response = handler.handleDatabaseUnavailable(
                new GraphUnavailableException(new RuntimeException("sensitive connection detail")));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("GRAPH_UNAVAILABLE", response.getBody().code());
        assertEquals(
                "The supply-chain graph is temporarily unavailable. Please try again shortly.",
                response.getBody().message());
    }
}
