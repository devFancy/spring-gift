package gift.support.response;

import gift.support.error.ErrorType;

// 모든 HTTP 응답을 success/error 두 형태로 감싼다. 새 도메인이 추가되어도 응답 구조는 그대로 유지된다.
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
