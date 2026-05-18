package gift.domain.option.policy;

import gift.support.error.CoreException;
import gift.support.error.ErrorType;

public final class OptionPolicy {

    private OptionPolicy() {
    }

    public static void ensureNameNotDuplicated(boolean alreadyExists) {
        if (alreadyExists) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "이미 존재하는 옵션명입니다.");
        }
    }

    public static void ensureNotLastOption(int currentOptionCount) {
        if (currentOptionCount <= 1) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "옵션이 1개인 상품은 옵션을 삭제할 수 없습니다.");
        }
    }
}
