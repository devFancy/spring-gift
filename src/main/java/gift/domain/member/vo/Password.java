package gift.domain.member.vo;

import gift.support.error.CoreException;
import gift.support.error.ErrorType;

public record Password(String value) {

    public Password {
        if (value == null || value.isBlank()) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "비밀번호는 필수입니다.");
        }
    }
}
