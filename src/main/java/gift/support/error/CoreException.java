package gift.support.error;

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
