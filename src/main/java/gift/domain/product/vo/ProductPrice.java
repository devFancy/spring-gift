package gift.domain.product.vo;

import gift.support.error.CoreException;
import gift.support.error.ErrorType;

public final class ProductPrice {

    private final int value;

    public ProductPrice(int value) {
        validate(value);
        this.value = value;
    }

    public int value() {
        return value;
    }

    private static void validate(int value) {
        if (value <= 0) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "상품 가격은 1원 이상이어야 합니다.");
        }
    }
}
