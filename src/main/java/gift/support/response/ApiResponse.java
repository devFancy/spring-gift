package gift.support.response;

import gift.support.error.ErrorType;

public record ApiResponse<T>(
    ResultType result,
    T data,
    ErrorBody error
) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResultType.SUCCESS, data, null);
    }

    public static ApiResponse<Void> error(ErrorType type, String message) {
        return new ApiResponse<>(ResultType.ERROR, null, new ErrorBody(type.code(), message));
    }

    public record ErrorBody(String code, String message) {
    }
}
