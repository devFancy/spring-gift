package gift.domain.member;

import gift.domain.member.vo.Email;
import gift.domain.member.vo.Password;
import gift.domain.member.vo.Point;
import gift.support.error.CoreException;
import gift.support.error.ErrorType;

public class Member {

    private final Long id;
    private Email email;
    private Password password;
    private String kakaoAccessToken;
    private Point point;

    public Member(Long id, String email, String password, String kakaoAccessToken, int point) {
        this.id = id;
        this.email = new Email(email);
        if (password != null) {
            this.password = new Password(password);
        }
        this.kakaoAccessToken = kakaoAccessToken;
        this.point = new Point(point);
    }

    public Member(String email, String password) {
        this(null, email, password, null, 0);
    }

    public Member(String email) {
        this(null, email, null, null, 0);
    }

    public void update(String email, String password) {
        this.email = new Email(email);
        this.password = new Password(password);
    }

    public void updateKakaoAccessToken(String kakaoAccessToken) {
        this.kakaoAccessToken = kakaoAccessToken;
    }

    public void chargePoint(int amount) {
        if (amount <= 0) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "충전 금액은 1 이상이어야 합니다.");
        }
        this.point = new Point(this.point.value() + amount);
    }

    public void deductPoint(int amount) {
        if (amount <= 0) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "차감 금액은 1 이상이어야 합니다.");
        }
        if (amount > this.point.value()) {
            throw new CoreException(ErrorType.CONFLICT, "포인트가 부족합니다.");
        }
        this.point = new Point(this.point.value() - amount);
    }

    public Long getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public Password getPassword() {
        return password;
    }

    public String getKakaoAccessToken() {
        return kakaoAccessToken;
    }

    public Point getPoint() {
        return point;
    }
}
