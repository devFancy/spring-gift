package gift.api.member;

import gift.IntegrationTestSupport;
import gift.storage.member.Member;
import gift.storage.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@DisplayName("회원 영역 작동 보존")
class MemberIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 가입 시 데이터베이스에 회원이 저장된다")
    void registerPersistsMember() throws Exception {
        String body = """
            {"email": "register@example.com", "password": "password1234"}
            """;

        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated());

        Optional<Member> saved = memberRepository.findByEmail("register@example.com");
        assertThat(saved).isPresent();
        assertThat(saved.get().getPassword()).isEqualTo("password1234");
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 가입하면 예외 응답이 반환된다")
    void registerWithDuplicateEmailRejected() throws Exception {
        memberRepository.save(new Member("dup@example.com", "old"));
        String body = """
            {"email": "dup@example.com", "password": "newpassword"}
            """;

        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());

        assertThat(memberRepository.count()).isEqualTo(1L);
    }

    @Test
    @DisplayName("올바른 이메일과 비밀번호로 로그인하면 성공 응답이 반환된다")
    void loginWithValidCredentialsSucceeds() throws Exception {
        memberRepository.save(new Member("login@example.com", "password1234"));
        String body = """
            {"email": "login@example.com", "password": "password1234"}
            """;

        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인하면 예외 응답이 반환된다")
    void loginWithInvalidPasswordRejected() throws Exception {
        memberRepository.save(new Member("login@example.com", "password1234"));
        String body = """
            {"email": "login@example.com", "password": "wrongpassword"}
            """;

        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }
}
