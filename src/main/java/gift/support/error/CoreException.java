package gift.support.error;

// 도메인 예외를 ErrorType 한 가지 축으로 모은다. 새 비즈니스 예외는 ErrorType 항목 추가로 표현하며 별도 RuntimeException 을 만들지 않는다.
public class CoreException extends RuntimeException {

    private final ErrorType errorType;

    public CoreException(ErrorType errorType) {
        super(errorType.message());
        this.errorType = errorType;
    }

    public CoreException(ErrorType errorType, String customMessage) {
        super(customMessage);
        this.errorType = errorType;
    }

    public ErrorType errorType() {
        return errorType;
    }
}
