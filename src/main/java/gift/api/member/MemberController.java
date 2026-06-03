package gift.api.member;

import gift.api.auth.AuthDto;
import gift.application.member.MemberService;
import gift.support.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthDto.Response>> register(@Valid @RequestBody MemberDto.Request request) {
        String token = memberService.register(request.email(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(new AuthDto.Response(token)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDto.Response>> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        String token = memberService.login(request.email(), request.password());
        return ResponseEntity.ok(ApiResponse.success(new AuthDto.Response(token)));
    }
}
