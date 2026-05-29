package gift.domain.product.policy;

import gift.support.error.CoreException;
import gift.support.error.ErrorType;

public final class ProductNamePolicy {

    private ProductNamePolicy() {
    }

    public static void validateKakaoUsage(String name) {
        if (name.contains("카카오")) {
            throw new CoreException(ErrorType.INVALID_REQUEST,
                "\"카카오\"가 포함된 상품명은 담당 MD와 협의한 경우에만 사용할 수 있습니다.");
        }
    }
}
