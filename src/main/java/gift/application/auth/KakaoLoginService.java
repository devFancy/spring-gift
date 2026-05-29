package gift.application.auth;

import gift.domain.member.Member;
import gift.domain.member.MemberRepository;
import gift.infrastructure.JwtProvider;
import gift.infrastructure.oauth.client.KakaoLoginClient;
import gift.infrastructure.oauth.dto.KakaoTokenResponse;
import gift.infrastructure.oauth.dto.KakaoUserResponse;
import gift.infrastructure.oauth.uri.KakaoOAuthUri;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class KakaoLoginService {

    private final KakaoOAuthUri kakaoOAuthUri;
    private final KakaoLoginClient kakaoLoginClient;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public KakaoLoginService(
        KakaoOAuthUri kakaoOAuthUri,
        KakaoLoginClient kakaoLoginClient,
        MemberRepository memberRepository,
        JwtProvider jwtProvider
    ) {
        this.kakaoOAuthUri = kakaoOAuthUri;
        this.kakaoLoginClient = kakaoLoginClient;
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
    }

    public String getAuthorizationUrl() {
        return kakaoOAuthUri.generateAuthorizationUri();
    }

    @Transactional
    public String handleCallback(String code) {
        KakaoTokenResponse tokenResponse = kakaoLoginClient.requestAccessToken(code);
        KakaoUserResponse userResponse = kakaoLoginClient.requestUserInfo(tokenResponse.accessToken());
        Member member = upsertMember(userResponse.email(), tokenResponse.accessToken());
        return jwtProvider.createToken(member.getEmail().value());
    }

    private Member upsertMember(String email, String kakaoAccessToken) {
        Member member = memberRepository.findByEmail(email).orElseGet(() -> new Member(email));
        member.updateKakaoAccessToken(kakaoAccessToken);
        return memberRepository.save(member);
    }
}
