package ai.pharmroute.api.common;

import java.time.Instant;

public record ApiError(Instant timestamp, int status, String code, String message) {
}
