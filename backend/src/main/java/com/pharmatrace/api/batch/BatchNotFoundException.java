package com.pharmatrace.api.batch;

public class BatchNotFoundException extends RuntimeException {

    public BatchNotFoundException(String batchId) {
        super("No medicine batch was found for ID " + batchId);
    }
}
