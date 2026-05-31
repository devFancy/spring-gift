package gift.domain.order.vo;

import gift.support.error.CoreException;
import gift.support.error.ErrorType;

public record Quantity(int value) {

    public Quantity {
        if (value < 1) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "주문 수량은 1 이상이어야 합니다.");
        }
    }
}
