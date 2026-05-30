package gift.support.error;

import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

public enum ErrorType {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "E400", "잘못된 요청입니다", LogLevel.INFO),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E401", "인증이 필요합니다", LogLevel.INFO),
    FORBIDDEN(HttpStatus.FORBIDDEN, "E403", "권한이 없습니다", LogLevel.INFO),
    NOT_FOUND(HttpStatus.NOT_FOUND, "E404", "리소스를 찾을 수 없습니다", LogLevel.INFO),
    CONFLICT(HttpStatus.CONFLICT, "E409", "요청이 현재 상태와 충돌합니다", LogLevel.INFO),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E500", "서버 내부 오류가 발생했습니다", LogLevel.ERROR);

    private final HttpStatus status;
    private final String code;
    private final String message;
    private final LogLevel logLevel;

    ErrorType(HttpStatus status, String code, String message, LogLevel logLevel) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.logLevel = logLevel;
    }

    public HttpStatus status() {
        return status;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

    public LogLevel logLevel() {
        return logLevel;
    }
}
