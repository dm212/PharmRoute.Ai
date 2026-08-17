package com.pharmatrace.api.batch;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/batches")
public class BatchController {

    private final BatchService service;

    public BatchController(BatchService service) {
        this.service = service;
    }

    @GetMapping("/{batchId}/investigation")
    public BatchInvestigation investigate(
            @PathVariable
            @Size(min = 3, max = 50)
            @Pattern(regexp = "[A-Za-z0-9-]+", message = "must contain only letters, numbers, and hyphens")
            String batchId) {
        return service.investigate(batchId);
    }
}
