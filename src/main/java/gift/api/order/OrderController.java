package gift.api.order;

import gift.application.order.OrderService;
import gift.auth.AuthenticationResolver;
import gift.storage.member.Member;
import gift.storage.order.Order;
import gift.support.error.CoreException;
import gift.support.error.ErrorType;
import gift.support.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final AuthenticationResolver authenticationResolver;

    public OrderController(OrderService orderService, AuthenticationResolver authenticationResolver) {
        this.orderService = orderService;
        this.authenticationResolver = authenticationResolver;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderDto.Response>>> getOrders(
        @RequestHeader("Authorization") String authorization,
        Pageable pageable
    ) {
        Member member = requireMember(authorization);
        Page<OrderDto.Response> orders = orderService.findByMember(member.getId(), pageable)
            .map(OrderDto.Response::from);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDto.Response>> createOrder(
        @RequestHeader("Authorization") String authorization,
        @Valid @RequestBody OrderDto.Request request
    ) {
        Member member = requireMember(authorization);
        Order saved = orderService.place(member.getId(), request.optionId(), request.quantity(), request.message());
        return ResponseEntity.status(HttpStatus.CREATED)
            .location(URI.create("/api/v1/orders/" + saved.getId()))
            .body(ApiResponse.success(OrderDto.Response.from(saved)));
    }

    private Member requireMember(String authorization) {
        Member member = authenticationResolver.extractMember(authorization);
        if (member == null) {
            throw new CoreException(ErrorType.UNAUTHORIZED, "인증이 필요합니다.");
        }
        return member;
    }
}
