package gift.domain.product.vo;

import gift.support.error.CoreException;
import gift.support.error.ErrorType;

public final class ProductImage {

    private final String value;

    public ProductImage(String value) {
        validate(value);
        this.value = value;
    }

    public String value() {
        return value;
    }

    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "상품 이미지 URL은 필수입니다.");
        }
    }
}
