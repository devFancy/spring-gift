package gift.api.auth;

import gift.RestDocsSupport;
import gift.application.auth.KakaoLoginService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("카카오 로그인 API 문서화")
class KakaoAuthControllerDocsTest extends RestDocsSupport {

    private final KakaoLoginService kakaoLoginService = mock(KakaoLoginService.class);

    @Override
    protected Object initController() {
        return new KakaoAuthController(kakaoLoginService);
    }

    @Test
    @DisplayName("카카오 로그인 페이지로 이동하면 인증 페이지로 리다이렉트된다")
    void login() throws Exception {
        given(kakaoLoginService.getAuthorizationUrl())
            .willReturn("https://kauth.kakao.com/oauth/authorize?client_id=xxx");

        mockMvc.perform(get("/api/v1/auth/kakao/login"))
            .andExpect(status().isFound())
            .andDo(document("auth/kakao-login"));
    }

    @Test
    @DisplayName("카카오 인증 후 콜백을 받으면 인증 토큰을 발급한다")
    void callback() throws Exception {
        given(kakaoLoginService.handleCallback(anyString())).willReturn("mock-token");

        mockMvc.perform(get("/api/v1/auth/kakao/callback")
                .param("code", "auth-code"))
            .andExpect(status().isOk())
            .andDo(document("auth/kakao-callback",
                responseFields(
                    fieldWithPath("result").description("SUCCESS / ERROR"),
                    fieldWithPath("data.token").description("발급된 인증 토큰"),
                    fieldWithPath("error").description("에러 정보 (성공 시 null)").optional()
                )
            ));
    }
}
