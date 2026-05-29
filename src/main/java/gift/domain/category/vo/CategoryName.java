package gift.domain.category.vo;

import gift.support.error.CoreException;
import gift.support.error.ErrorType;

public record CategoryName(String value) {

    public CategoryName {
        if (value == null || value.isBlank()) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "카테고리 이름은 필수입니다.");
        }
    }
}
