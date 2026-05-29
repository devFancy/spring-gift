package gift.domain.product.vo;

import gift.support.error.CoreException;
import gift.support.error.ErrorType;

public record ProductImage(String value) {

    public ProductImage {
        if (value == null || value.isBlank()) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "상품 이미지 URL은 필수입니다.");
        }
    }
}
