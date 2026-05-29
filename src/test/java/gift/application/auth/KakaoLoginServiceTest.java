package gift.application.auth;

import gift.IntegrationTestSupport;
import gift.domain.member.Member;
import gift.domain.member.MemberRepository;
import gift.infrastructure.oauth.client.KakaoLoginClient;
import gift.infrastructure.oauth.dto.KakaoTokenResponse;
import gift.infrastructure.oauth.dto.KakaoUserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@DisplayName("카카오 로그인 흐름")
class KakaoLoginServiceTest extends IntegrationTestSupport {

    @Autowired
    private KakaoLoginService kakaoLoginService;

    @Autowired
    private MemberRepository memberRepository;

    @MockitoBean
    private KakaoLoginClient kakaoLoginClient;

    @Nested
    @DisplayName("카카오 콜백을 처리할 때")
    class HandleCallback {

        @Test
        @DisplayName("같은 카카오 계정으로 다시 콜백되면 회원은 한 명만 유지되고 토큰만 갱신된다")
        void idempotentCallback() {
            // given
            String email = "kakao-user@example.com";
            given(kakaoLoginClient.requestAccessToken(anyString()))
                .willReturn(new KakaoTokenResponse("first-token"))
                .willReturn(new KakaoTokenResponse("second-token"));
            given(kakaoLoginClient.requestUserInfo(anyString()))
                .willReturn(new KakaoUserResponse(new KakaoUserResponse.KakaoAccount(email)));

            // when
            kakaoLoginService.handleCallback("code-1");
            kakaoLoginService.handleCallback("code-2");

            // then
            List<Member> members = memberRepository.findAll();
            assertThat(members).hasSize(1);
            Member onlyMember = members.get(0);
            assertThat(onlyMember.getEmail().value()).isEqualTo(email);
            assertThat(onlyMember.getKakaoAccessToken()).isEqualTo("second-token");
        }
    }
}
