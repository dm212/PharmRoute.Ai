package com.pharmatrace.api.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BatchServiceTest {

    @Mock
    private BatchRepository repository;

    @Test
    void normalizesBatchIdBeforeQueryingRepository() {
        BatchInvestigation expected = new BatchInvestigation(null, List.of(), List.of(), List.of());
        when(repository.investigate("BT-2026-0812-A17")).thenReturn(Optional.of(expected));

        BatchInvestigation actual = new BatchService(repository).investigate("  bt-2026-0812-a17  ");

        assertSame(expected, actual);
        verify(repository).investigate("BT-2026-0812-A17");
    }

    @Test
    void raisesClearNotFoundErrorForUnknownBatch() {
        when(repository.investigate("UNKNOWN-01")).thenReturn(Optional.empty());

        BatchNotFoundException exception = assertThrows(
                BatchNotFoundException.class,
                () -> new BatchService(repository).investigate("unknown-01"));

        assertEquals("No medicine batch was found for ID UNKNOWN-01", exception.getMessage());
    }
}
