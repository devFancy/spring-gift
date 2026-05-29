package gift.domain.member.vo;

import gift.support.error.CoreException;
import gift.support.error.ErrorType;

public final class Password {

    private final String value;

    public Password(String value) {
        validate(value);
        this.value = value;
    }

    public String value() {
        return value;
    }

    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "비밀번호는 필수입니다.");
        }
    }
}
