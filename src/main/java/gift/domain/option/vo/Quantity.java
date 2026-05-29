package gift.domain.option.vo;

import gift.support.error.CoreException;
import gift.support.error.ErrorType;

public final class Quantity {

    private final int value;

    public Quantity(int value) {
        validate(value);
        this.value = value;
    }

    public int value() {
        return value;
    }

    private static void validate(int value) {
        if (value < 0) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "수량은 0 이상이어야 합니다.");
        }
    }
}
