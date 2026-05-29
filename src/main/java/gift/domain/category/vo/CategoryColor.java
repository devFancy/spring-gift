package gift.domain.category.vo;

import gift.support.error.CoreException;
import gift.support.error.ErrorType;

public record CategoryColor(String value) {

    public CategoryColor {
        if (value == null || value.isBlank()) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "카테고리 색상은 필수입니다.");
        }
    }
}
