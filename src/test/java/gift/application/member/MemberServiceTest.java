package gift.application.member;

import gift.IntegrationTestSupport;
import gift.domain.member.Member;
import gift.domain.member.MemberRepository;
import gift.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("회원")
class MemberServiceTest extends IntegrationTestSupport {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("운영자가 회원을 등록하면 회원으로 등록된다")
    void persistsMember() {
        // when
        Member saved = memberService.createForAdmin("admin@example.com", "password");

        // then
        assertThat(memberRepository.findById(saved.getId())).isPresent();
        assertThat(saved.getEmail().value()).isEqualTo("admin@example.com");
    }

    @Test
    @DisplayName("회원이 포인트를 충전하면 충전 금액만큼 보유 포인트가 늘어난다")
    void increasesBalance() {
        // given
        Member member = memberService.createForAdmin("point@example.com", "pw");

        // when
        Member updated = memberService.chargePoint(member.getId(), 10000);

        // then
        assertThat(updated.getPoint().value()).isEqualTo(10000);
    }

    @Test
    @DisplayName("포인트를 충전할 때 충전 금액이 0 이하이면 예외가 발생한다")
    void rejectsNonPositiveAmount() {
        // given
        Member member = memberService.createForAdmin("point@example.com", "pw");

        // when, then
        assertThatThrownBy(() -> memberService.chargePoint(member.getId(), 0))
            .isInstanceOf(CoreException.class)
            .hasMessageContaining("충전 금액");
    }
}
