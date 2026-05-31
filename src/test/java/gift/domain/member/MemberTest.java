package gift.domain.member;

import gift.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("회원")
class MemberTest {

    @Test
    @DisplayName("이메일과 비밀번호가 유효하면 생성된다")
    void createSuccess() {
        // when
        Member member = new Member("test@test.com", "password123");

        // then
        assertThat(member.getEmail().value()).isEqualTo("test@test.com");
        assertThat(member.getPoint().value()).isZero();
    }

    @Test
    @DisplayName("이메일에 @ 가 없으면 예외가 발생한다")
    void emailWithoutAtThrows() {
        assertThatThrownBy(() -> new Member("invalidemail", "password"))
            .isInstanceOf(CoreException.class);
    }

    @Test
    @DisplayName("이메일이 비어 있으면 예외가 발생한다")
    void emailBlankThrows() {
        assertThatThrownBy(() -> new Member("", "password"))
            .isInstanceOf(CoreException.class);
    }

    @Test
    @DisplayName("비밀번호가 비어 있으면 예외가 발생한다")
    void passwordBlankThrows() {
        assertThatThrownBy(() -> new Member("test@test.com", ""))
            .isInstanceOf(CoreException.class);
    }

    @Test
    @DisplayName("양수 금액을 충전하면 포인트가 증가한다")
    void chargePointSuccess() {
        // given
        Member member = new Member("test@test.com", "password");

        // when
        member.chargePoint(5000);

        // then
        assertThat(member.getPoint().value()).isEqualTo(5000);
    }

    @Test
    @DisplayName("0 이하 금액을 충전하면 예외가 발생한다")
    void chargeZeroThrows() {
        // given
        Member member = new Member("test@test.com", "password");

        // when & then
        assertThatThrownBy(() -> member.chargePoint(0))
            .isInstanceOf(CoreException.class);
    }

    @Test
    @DisplayName("잔액 이하 금액을 차감하면 포인트가 감소한다")
    void deductPointSuccess() {
        // given
        Member member = new Member("test@test.com", "password");
        member.chargePoint(10000);

        // when
        member.deductPoint(3000);

        // then
        assertThat(member.getPoint().value()).isEqualTo(7000);
    }

    @Test
    @DisplayName("잔액보다 많은 금액을 차감하면 예외가 발생한다")
    void deductMoreThanBalanceThrows() {
        // given
        Member member = new Member("test@test.com", "password");
        member.chargePoint(1000);

        // when & then
        assertThatThrownBy(() -> member.deductPoint(2000))
            .isInstanceOf(CoreException.class);
    }

    @Test
    @DisplayName("0 이하 금액을 차감하면 예외가 발생한다")
    void deductZeroThrows() {
        // given
        Member member = new Member("test@test.com", "password");

        // when & then
        assertThatThrownBy(() -> member.deductPoint(0))
            .isInstanceOf(CoreException.class);
    }
}
