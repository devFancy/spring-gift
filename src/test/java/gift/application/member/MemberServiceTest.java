package gift.application.member;

import gift.IntegrationTestSupport;
import gift.domain.member.Member;
import gift.domain.member.MemberRepository;
import gift.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("회원 영역 흐름")
class MemberServiceTest extends IntegrationTestSupport {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Nested
    @DisplayName("운영자가 회원을 등록할 때")
    class CreateForAdmin {

        @Test
        @DisplayName("회원이 데이터베이스에 저장된다")
        void persistsMember() {
            // when
            Member saved = memberService.createForAdmin("admin@example.com", "password");

            // then
            assertThat(memberRepository.findById(saved.getId())).isPresent();
            assertThat(saved.getEmail().value()).isEqualTo("admin@example.com");
        }
    }

    @Nested
    @DisplayName("회원이 포인트를 충전할 때")
    class ChargePoint {

        @Test
        @DisplayName("충전 금액만큼 보유 포인트가 늘어난다")
        void increasesBalance() {
            // given
            Member member = memberService.createForAdmin("point@example.com", "pw");

            // when
            Member updated = memberService.chargePoint(member.getId(), 10000);

            // then
            assertThat(updated.getPoint()).isEqualTo(10000);
        }

        @Test
        @DisplayName("충전 금액이 0 이하이면 예외가 발생한다")
        void rejectsNonPositiveAmount() {
            // given
            Member member = memberService.createForAdmin("point@example.com", "pw");

            // when, then
            assertThatThrownBy(() -> memberService.chargePoint(member.getId(), 0))
                .isInstanceOf(CoreException.class)
                .hasMessageContaining("충전 금액");
        }
    }
}
