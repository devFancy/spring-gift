package gift.api.member;

import gift.IntegrationTestSupport;
import gift.application.member.MemberService;
import gift.domain.member.Member;
import gift.domain.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@DisplayName("회원 가입과 로그인")
class MemberControllerTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Nested
    @DisplayName("신규 사용자가 가입할 때")
    class Register {

        @Test
        @DisplayName("회원으로 등록되고 인증 토큰을 받는다")
        void registersWithToken() throws Exception {
            String body = """
                {"email": "register@example.com", "password": "password1234"}
                """;

            mockMvc.perform(post("/api/v1/members/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isCreated())
                .andDo(document("v1/members/register"));

            Optional<Member> saved = memberRepository.findByEmail("register@example.com");
            assertThat(saved).isPresent();
            assertThat(saved.get().getPassword()).isNotEqualTo("password1234");
            assertThat(saved.get().getPassword()).isNotBlank();
        }

        @Test
        @DisplayName("이미 가입된 이메일이면 예외가 발생한다")
        void rejectsDuplicateEmail() throws Exception {
            memberRepository.save(new Member("dup@example.com", "old"));
            String body = """
                {"email": "dup@example.com", "password": "newpassword"}
                """;

            mockMvc.perform(post("/api/v1/members/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isBadRequest())
                .andDo(document("v1/members/register-rejected-duplicate"));

            assertThat(memberRepository.findAll()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("기존 회원이 로그인할 때")
    class Login {

        @Test
        @DisplayName("올바른 자격 증명이면 인증 토큰을 받는다")
        void returnsTokenForValidCredentials() throws Exception {
            memberService.createForAdmin("login@example.com", "password1234");
            String body = """
                {"email": "login@example.com", "password": "password1234"}
                """;

            mockMvc.perform(post("/api/v1/members/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isOk())
                .andDo(document("v1/members/login"));
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않으면 예외가 발생한다")
        void rejectsInvalidPassword() throws Exception {
            memberService.createForAdmin("login@example.com", "password1234");
            String body = """
                {"email": "login@example.com", "password": "wrongpassword"}
                """;

            mockMvc.perform(post("/api/v1/members/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isBadRequest())
                .andDo(document("v1/members/login-rejected"));
        }
    }
}
