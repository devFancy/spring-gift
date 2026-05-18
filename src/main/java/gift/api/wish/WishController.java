package gift.api.wish;

import gift.application.wish.WishService;
import gift.api.resolver.AuthenticationResolver;
import gift.storage.member.Member;
import gift.storage.wish.Wish;
import gift.support.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/wishes")
public class WishController {

    private final WishService wishService;
    private final AuthenticationResolver authenticationResolver;

    public WishController(WishService wishService, AuthenticationResolver authenticationResolver) {
        this.wishService = wishService;
        this.authenticationResolver = authenticationResolver;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<WishDto.Response>>> getWishes(
        @RequestHeader("Authorization") String authorization,
        Pageable pageable
    ) {
        Member member = authenticationResolver.requireMember(authorization);
        Page<WishDto.Response> wishes = wishService.findByMember(member.getId(), pageable).map(WishDto.Response::from);
        return ResponseEntity.ok(ApiResponse.success(wishes));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WishDto.Response>> addWish(
        @RequestHeader("Authorization") String authorization,
        @Valid @RequestBody WishDto.Request request
    ) {
        Member member = authenticationResolver.requireMember(authorization);
        Wish wish = wishService.add(member.getId(), request.productId());
        return ResponseEntity.status(HttpStatus.CREATED)
            .location(URI.create("/api/v1/wishes/" + wish.getId()))
            .body(ApiResponse.success(WishDto.Response.from(wish)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeWish(
        @RequestHeader("Authorization") String authorization,
        @PathVariable Long id
    ) {
        Member member = authenticationResolver.requireMember(authorization);
        wishService.remove(member.getId(), id);
        return ResponseEntity.noContent().build();
    }

}
