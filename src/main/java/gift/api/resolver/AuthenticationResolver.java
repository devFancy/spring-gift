package gift.api.resolver;

import gift.domain.member.Member;
import gift.domain.member.MemberRepository;
import gift.infrastructure.JwtProvider;
import gift.support.error.CoreException;
import gift.support.error.ErrorType;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationResolver {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    public AuthenticationResolver(JwtProvider jwtProvider, MemberRepository memberRepository) {
        this.jwtProvider = jwtProvider;
        this.memberRepository = memberRepository;
    }

    public Member requireMember(String authorization) {
        String email = parseEmail(authorization);
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new CoreException(ErrorType.UNAUTHORIZED, "인증이 필요합니다."));
    }

    private String parseEmail(String authorization) {
        if (authorization == null) {
            throw new CoreException(ErrorType.UNAUTHORIZED, "인증이 필요합니다.");
        }
        try {
            String token = authorization.replace("Bearer ", "");
            return jwtProvider.getEmail(token);
        } catch (Exception cause) {
            throw new CoreException(ErrorType.UNAUTHORIZED, "인증이 필요합니다.");
        }
    }
}
