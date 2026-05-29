package gift.domain.category.vo;

import gift.support.error.CoreException;
import gift.support.error.ErrorType;

public record CategoryImage(String value) {

    public CategoryImage {
        if (value == null || value.isBlank()) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "카테고리 이미지 URL은 필수입니다.");
        }
    }
}
