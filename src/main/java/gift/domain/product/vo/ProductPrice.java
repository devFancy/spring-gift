package gift.domain.product.vo;

import gift.support.error.CoreException;
import gift.support.error.ErrorType;

public record ProductPrice(int value) {

    public ProductPrice {
        if (value <= 0) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "상품 가격은 1원 이상이어야 합니다.");
        }
    }
}
