package fedod.auth.service.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path,
                            List<String> details) {
}
