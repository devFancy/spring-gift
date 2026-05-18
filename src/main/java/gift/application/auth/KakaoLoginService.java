package gift.application.auth;

import gift.infrastructure.auth.JwtProvider;
import gift.infrastructure.oauth.client.KakaoLoginClient;
import gift.infrastructure.oauth.dto.KakaoTokenResponse;
import gift.infrastructure.oauth.dto.KakaoUserResponse;
import gift.infrastructure.oauth.uri.KakaoOAuthUri;
import gift.storage.member.Member;
import gift.storage.member.MemberRepository;
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
        return jwtProvider.createToken(member.getEmail());
    }

    private Member upsertMember(String email, String kakaoAccessToken) {
        Member member = memberRepository.findByEmail(email).orElseGet(() -> new Member(email));
        member.updateKakaoAccessToken(kakaoAccessToken);
        return memberRepository.save(member);
    }
}
