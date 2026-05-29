package gift.api.member;

import gift.IntegrationTestSupport;
import gift.domain.member.Member;
import gift.domain.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@DisplayName("운영자 회원 화면")
class AdminMemberControllerTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Nested
    @DisplayName("회원 등록 폼을 제출할 때")
    class CreateForm {

        @Test
        @DisplayName("운영자가 이메일과 비밀번호를 입력해 회원을 등록하면 저장된다")
        void savesWhenValidInputProvided() throws Exception {
            // when
            mockMvc.perform(post("/admin/members")
                    .param("email", "admin-user@example.com")
                    .param("password", "password1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/members"));

            // then
            Optional<Member> saved = memberRepository.findByEmail("admin-user@example.com");
            assertThat(saved).isPresent();
            assertThat(saved.get().getPassword()).isNotEqualTo("password1234");
            assertThat(saved.get().getPassword()).isNotBlank();
        }
    }
}
