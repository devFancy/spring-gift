package gift.support.error;

import gift.support.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler {

    @ExceptionHandler(CoreException.class)
    public ResponseEntity<ApiResponse<Void>> handleCoreException(CoreException exception) {
        ErrorType type = exception.errorType();
        ApiResponse<Void> body = ApiResponse.error(type, exception.getMessage());
        return ResponseEntity.status(type.status()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .collect(Collectors.joining(", "));
        ApiResponse<Void> body = ApiResponse.error(ErrorType.INVALID_REQUEST, message);
        return ResponseEntity.status(ErrorType.INVALID_REQUEST.status()).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException exception) {
        ApiResponse<Void> body = ApiResponse.error(ErrorType.INVALID_REQUEST, exception.getMessage());
        return ResponseEntity.status(ErrorType.INVALID_REQUEST.status()).body(body);
    }
}
