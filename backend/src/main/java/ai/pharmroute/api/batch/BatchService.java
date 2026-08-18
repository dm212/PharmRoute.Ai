package ai.pharmroute.api.batch;

import java.util.Locale;

import org.springframework.stereotype.Service;

@Service
public class BatchService {

    private final BatchRepository repository;

    public BatchService(BatchRepository repository) {
        this.repository = repository;
    }

    public BatchInvestigation investigate(String batchId) {
        String normalizedId = batchId.trim().toUpperCase(Locale.ROOT);
        return repository.investigate(normalizedId)
                .orElseThrow(() -> new BatchNotFoundException(normalizedId));
    }
}
