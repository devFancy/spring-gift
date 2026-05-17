package gift.api.member;

import gift.application.member.MemberService;
import gift.auth.TokenResponse;
import gift.support.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<TokenResponse>> register(@Valid @RequestBody MemberDto.Request request) {
        TokenResponse tokenResponse = memberService.register(request.email(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(tokenResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody MemberDto.Request request) {
        TokenResponse tokenResponse = memberService.login(request.email(), request.password());
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }
}
