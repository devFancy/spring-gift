package gift.api.member;

import gift.RestDocsSupport;
import gift.application.member.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("회원 가입과 로그인 API 문서화")
class MemberControllerDocsTest extends RestDocsSupport {

    private final MemberService memberService = mock(MemberService.class);

    @Override
    protected Object initController() {
        return new MemberController(memberService);
    }

    @Test
    @DisplayName("신규 회원이 가입하면 인증 토큰을 받는다")
    void register() throws Exception {
        // given
        given(memberService.register(any(), any())).willReturn("mock-token");

        String body = """
            {"email": "member@example.com", "password": "password1234"}
            """;

        // when & then
        mockMvc.perform(post("/api/v1/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andDo(document("member/register",
                requestFields(
                    fieldWithPath("email").description("회원 이메일"),
                    fieldWithPath("password").description("비밀번호")
                ),
                responseFields(
                    fieldWithPath("result").description("SUCCESS / ERROR"),
                    fieldWithPath("data.token").description("발급된 인증 토큰"),
                    fieldWithPath("error").description("에러 정보 (성공 시 null)").optional()
                )
            ));
    }

    @Test
    @DisplayName("기존 회원이 로그인하면 인증 토큰을 받는다")
    void login() throws Exception {
        // given
        given(memberService.login(any(), any())).willReturn("mock-token");

        String body = """
            {"email": "member@example.com", "password": "password1234"}
            """;

        // when & then
        mockMvc.perform(post("/api/v1/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andDo(document("member/login",
                requestFields(
                    fieldWithPath("email").description("회원 이메일"),
                    fieldWithPath("password").description("비밀번호")
                ),
                responseFields(
                    fieldWithPath("result").description("SUCCESS / ERROR"),
                    fieldWithPath("data.token").description("발급된 인증 토큰"),
                    fieldWithPath("error").description("에러 정보 (성공 시 null)").optional()
                )
            ));
    }
}
