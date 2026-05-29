package gift.domain.option.vo;

import gift.support.error.CoreException;
import gift.support.error.ErrorType;

public record Quantity(int value) {

    public Quantity {
        if (value < 0) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "수량은 0 이상이어야 합니다.");
        }
    }
}
