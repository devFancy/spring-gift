package gift.support.error;

import gift.support.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CoreException.class)
    public ResponseEntity<ApiResponse<Void>> handleCoreException(CoreException exception) {
        ErrorType type = exception.errorType();
        logByLevel(type, exception);
        return ResponseEntity.status(type.status()).body(ApiResponse.error(type, exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .collect(Collectors.joining(", "));
        logByLevel(ErrorType.INVALID_REQUEST, exception);
        return ResponseEntity.status(ErrorType.INVALID_REQUEST.status())
            .body(ApiResponse.error(ErrorType.INVALID_REQUEST, message));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException exception) {
        logByLevel(ErrorType.INVALID_REQUEST, exception);
        return ResponseEntity.status(ErrorType.INVALID_REQUEST.status())
            .body(ApiResponse.error(ErrorType.INVALID_REQUEST, exception.getMessage()));
    }

    private void logByLevel(ErrorType type, Exception exception) {
        if (type.logLevel() == LogLevel.ERROR) {
            log.error("[{}] {}", type.code(), exception.getMessage(), exception);
            return;
        }
        if (type.logLevel() == LogLevel.WARN) {
            log.warn("[{}] {}", type.code(), exception.getMessage());
            return;
        }
        log.info("[{}] {}", type.code(), exception.getMessage());
    }
}
