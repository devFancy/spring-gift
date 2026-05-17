package gift.support.error;

import gift.support.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CoreException.class)
    public ResponseEntity<ApiResponse<Void>> handleCoreException(CoreException exception) {
        ErrorType type = exception.errorType();
        ApiResponse<Void> body = ApiResponse.error(type, exception.getMessage());
        return ResponseEntity.status(type.status()).body(body);
    }
}
