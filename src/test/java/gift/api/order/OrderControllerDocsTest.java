package gift.api.order;

import gift.RestDocsSupport;
import gift.api.resolver.AuthenticationResolver;
import gift.application.order.OrderService;
import gift.domain.category.Category;
import gift.domain.member.Member;
import gift.domain.option.Option;
import gift.domain.order.Order;
import gift.domain.product.Product;
import gift.infrastructure.SystemClockHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("선물 주문 API 문서화")
class OrderControllerDocsTest extends RestDocsSupport {

    private final OrderService orderService = mock(OrderService.class);
    private final AuthenticationResolver authenticationResolver = mock(AuthenticationResolver.class);

    @Override
    protected Object initController() {
        return new OrderController(orderService, authenticationResolver);
    }

    @Test
    @DisplayName("회원이 주문을 생성하면 주문이 저장된다")
    void createOrder() throws Exception {
        // given
        Member member = new Member(1L, "test@test.com", "password", null, 100_000);
        Category category = new Category(1L, "음료", "#FF0000", "img", null);
        Product product = new Product(1L, "아이스 아메리카노", 4500, "img", category);
        Option option = new Option(1L, product, "L 사이즈", 10);
        Order order = new Order(1L, option, member.getId(), 2, "선물입니다", LocalDateTime.of(2026, 5, 31, 12, 0));

        given(authenticationResolver.requireMember(anyString())).willReturn(member);
        given(orderService.place(anyLong(), anyLong(), anyInt(), any())).willReturn(order);

        String body = """
            {"optionId": 1, "quantity": 2, "message": "선물입니다"}
            """;

        // when & then
        mockMvc.perform(post("/api/v1/orders")
                .header("Authorization", "Bearer mock-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andDo(document("order/create",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer 인증 토큰")
                ),
                requestFields(
                    fieldWithPath("optionId").description("주문할 상품 옵션 ID"),
                    fieldWithPath("quantity").description("주문 수량 (1 이상)"),
                    fieldWithPath("message").description("선물 메시지").optional()
                ),
                responseFields(
                    fieldWithPath("result").description("SUCCESS / ERROR"),
                    fieldWithPath("data.id").description("주문 ID"),
                    fieldWithPath("data.optionId").description("상품 옵션 ID"),
                    fieldWithPath("data.quantity").description("주문 수량"),
                    fieldWithPath("data.message").description("선물 메시지").optional(),
                    fieldWithPath("data.orderDateTime").description("주문 일시"),
                    fieldWithPath("error").description("에러 정보 (성공 시 null)").optional()
                )
            ));
    }
}
