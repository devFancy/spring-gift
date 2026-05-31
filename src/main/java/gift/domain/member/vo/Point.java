package gift.domain.member.vo;

import gift.support.error.CoreException;
import gift.support.error.ErrorType;

public record Point(int value) {

    public Point {
        if (value < 0) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "포인트는 0 이상이어야 합니다.");
        }
    }
}
