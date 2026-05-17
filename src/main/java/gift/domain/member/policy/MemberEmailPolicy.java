package gift.domain.member.policy;

import gift.support.error.CoreException;
import gift.support.error.ErrorType;

public final class MemberEmailPolicy {

    private MemberEmailPolicy() {
    }

    public static void ensureNotDuplicated(boolean alreadyExists) {
        if (alreadyExists) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "이미 가입된 이메일입니다.");
        }
    }
}
