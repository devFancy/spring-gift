package gift.domain.product.vo;

import gift.support.error.CoreException;
import gift.support.error.ErrorType;

import java.util.regex.Pattern;

public final class ProductName {

    private static final int MAX_LENGTH = 15;
    private static final Pattern ALLOWED_PATTERN =
        Pattern.compile("^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ ()\\[\\]+\\-&/_]*$");

    private final String value;

    public ProductName(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "상품 이름은 필수입니다.");
        }
        if (value.length() > MAX_LENGTH) {
            throw new CoreException(ErrorType.INVALID_REQUEST,
                "상품 이름은 공백을 포함하여 최대 15자까지 입력할 수 있습니다.");
        }
        if (!ALLOWED_PATTERN.matcher(value).matches()) {
            throw new CoreException(ErrorType.INVALID_REQUEST,
                "상품 이름에 허용되지 않는 특수 문자가 포함되어 있습니다. 사용 가능: ( ), [ ], +, -, &, /, _");
        }
    }

    public String value() {
        return value;
    }
}
