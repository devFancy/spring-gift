package gift.api.auth;

import gift.application.auth.KakaoLoginService;
import gift.support.response.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/kakao")
public class KakaoAuthController {

    private final KakaoLoginService kakaoLoginService;

    public KakaoAuthController(KakaoLoginService kakaoLoginService) {
        this.kakaoLoginService = kakaoLoginService;
    }

    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        return ResponseEntity.status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, kakaoLoginService.getAuthorizationUrl())
            .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<ApiResponse<AuthDto.Response>> callback(@RequestParam("code") String code) {
        String token = kakaoLoginService.handleCallback(code);
        return ResponseEntity.ok(ApiResponse.success(new AuthDto.Response(token)));
    }
}
