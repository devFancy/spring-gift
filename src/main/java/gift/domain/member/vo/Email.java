package gift.domain.member.vo;

import gift.support.error.CoreException;
import gift.support.error.ErrorType;

public final class Email {

    private final String value;

    public Email(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "이메일은 필수입니다.");
        }
        if (!value.contains("@")) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "올바른 이메일 형식이 아닙니다.");
        }
    }

    public String value() {
        return value;
    }
}
