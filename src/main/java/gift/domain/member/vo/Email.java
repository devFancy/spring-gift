package gift.domain.member.vo;

import gift.support.error.CoreException;
import gift.support.error.ErrorType;

public record Email(String value) {

    public Email {
        if (value == null || value.isBlank()) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "이메일은 필수입니다.");
        }
        if (!value.contains("@")) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "올바른 이메일 형식이 아닙니다.");
        }
    }
}
