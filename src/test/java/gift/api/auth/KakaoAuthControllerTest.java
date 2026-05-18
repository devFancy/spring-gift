package gift.api.auth;

import gift.IntegrationTestSupport;
import gift.auth.KakaoLoginClient;
import gift.storage.member.Member;
import gift.storage.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@DisplayName("카카오 로그인")
class KakaoAuthControllerTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @MockitoBean
    private KakaoLoginClient kakaoLoginClient;

    @Nested
    @DisplayName("사용자가 카카오 로그인 페이지로 이동할 때")
    class StartLogin {

        @Test
        @DisplayName("카카오 인증 페이지로 리다이렉트된다")
        void redirectsToKakao() throws Exception {
            // when, then
            mockMvc.perform(get("/api/auth/kakao/login"))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION,
                    containsString("kauth.kakao.com/oauth/authorize")));
        }
    }

    @Nested
    @DisplayName("카카오 인증 후 콜백을 받을 때")
    class HandleCallback {

        @Test
        @DisplayName("새 이메일이면 회원이 자동 등록되고 인증 토큰을 받는다")
        void newEmailAutoRegisters() throws Exception {
            // given
            String email = "new-user@kakao.example";
            given(kakaoLoginClient.requestAccessToken(anyString()))
                .willReturn(new KakaoLoginClient.KakaoTokenResponse("access-token-stub"));
            given(kakaoLoginClient.requestUserInfo(anyString()))
                .willReturn(new KakaoLoginClient.KakaoUserResponse(
                    new KakaoLoginClient.KakaoUserResponse.KakaoAccount(email)
                ));

            // when
            mockMvc.perform(get("/api/auth/kakao/callback").param("code", "auth-code"))
                .andExpect(status().isOk());

            // then
            Optional<Member> saved = memberRepository.findByEmail(email);
            assertThat(saved).isPresent();
            assertThat(saved.get().getKakaoAccessToken()).isEqualTo("access-token-stub");
        }
    }
}
