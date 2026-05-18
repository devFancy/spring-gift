package gift.support.error;

import gift.support.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

// 도메인 예외(CoreException)와 검증 예외를 ApiResponse.error 한 가지 형태로 매핑.
// 새 예외 종류를 추가할 때는 @ExceptionHandler 메서드를 같은 패턴으로 한 줄 더 추가한다.
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
