package gift.support.error;

import org.springframework.http.HttpStatus;

// E{HTTP_STATUS}_{NNN} 형식. 같은 status 안의 비즈니스 케이스를 코드 시퀀스로 세분화하기 위함.
public enum ErrorType {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "E400_001", "잘못된 요청입니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E401_001", "인증이 필요합니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "E403_001", "권한이 없습니다"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "E404_001", "리소스를 찾을 수 없습니다"),
    CONFLICT(HttpStatus.CONFLICT, "E409_001", "요청이 현재 상태와 충돌합니다"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E500_001", "서버 내부 오류가 발생했습니다");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorType(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
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
}
