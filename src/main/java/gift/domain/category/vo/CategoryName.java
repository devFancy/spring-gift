package gift.domain.category.vo;

import gift.support.error.CoreException;
import gift.support.error.ErrorType;

public final class CategoryName {

    private final String value;

    public CategoryName(String value) {
        validate(value);
        this.value = value;
    }

    public String value() {
        return value;
    }

    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "카테고리 이름은 필수입니다.");
        }
    }
}
