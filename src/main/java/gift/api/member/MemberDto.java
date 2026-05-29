package gift.api.member;

import gift.domain.member.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public final class MemberDto {

    private MemberDto() {
    }

    public record Request(
        @NotBlank @Email String email,
        @NotBlank String password
    ) {
    }

    public record Response(
        Long id,
        String email,
        int point
    ) {
        public static Response from(Member member) {
            return new Response(member.getId(), member.getEmail().value(), member.getPoint());
        }
    }
}
