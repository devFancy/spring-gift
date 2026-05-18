package gift.infrastructure.oauth.uri;

import gift.config.kakao.KakaoLoginProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

// 카카오 OAuth 진입 URL 을 한 곳에서 조립. 새 OAuth 공급자 추가 시 같은 위치에 *Uri 클래스 한 개씩 둔다.
@Component
public class KakaoOAuthUri {

    private final KakaoLoginProperties properties;

    public KakaoOAuthUri(KakaoLoginProperties properties) {
        this.properties = properties;
    }

    public String generateAuthorizationUri() {
        return UriComponentsBuilder.fromUriString("https://kauth.kakao.com/oauth/authorize")
            .queryParam("response_type", "code")
            .queryParam("client_id", properties.clientId())
            .queryParam("redirect_uri", properties.redirectUri())
            .queryParam("scope", "account_email,talk_message")
            .build()
            .toUriString();
    }
}
