package gift.api.wish;

import gift.RestDocsSupport;
import gift.api.resolver.AuthenticationResolver;
import gift.application.wish.WishService;
import gift.domain.category.Category;
import gift.domain.member.Member;
import gift.domain.product.Product;
import gift.domain.wish.Wish;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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

@DisplayName("선물 위시리스트 API 문서화")
class WishControllerDocsTest extends RestDocsSupport {

    private final WishService wishService = mock(WishService.class);
    private final AuthenticationResolver authenticationResolver = mock(AuthenticationResolver.class);

    @Override
    protected Object initController() {
        return new WishController(wishService, authenticationResolver);
    }

    @Test
    @DisplayName("회원이 상품을 위시리스트에 담으면 위시 항목이 저장된다")
    void addWish() throws Exception {
        // given
        Member member = new Member(1L, "test@test.com", "password", null, 0);
        Category category = new Category(1L, "음료", "#FF0000", "img", null);
        Product product = new Product(1L, "아이스 아메리카노", 4500, "img", category);
        Wish wish = new Wish(1L, member.getId(), product);

        given(authenticationResolver.requireMember(anyString())).willReturn(member);
        given(wishService.add(anyLong(), anyLong())).willReturn(wish);

        String body = """
            {"productId": 1}
            """;

        // when & then
        mockMvc.perform(post("/api/v1/wishes")
                .header("Authorization", "Bearer mock-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andDo(document("wish/add",
                requestHeaders(
                    headerWithName("Authorization").description("Bearer 인증 토큰")
                ),
                requestFields(
                    fieldWithPath("productId").description("위시리스트에 담을 상품 ID")
                ),
                responseFields(
                    fieldWithPath("result").description("SUCCESS / ERROR"),
                    fieldWithPath("data.id").description("위시 항목 ID"),
                    fieldWithPath("data.productId").description("상품 ID"),
                    fieldWithPath("data.productName").description("상품명"),
                    fieldWithPath("data.price").description("상품 가격"),
                    fieldWithPath("data.imageUrl").description("상품 이미지 URL"),
                    fieldWithPath("error").description("에러 정보 (성공 시 null)").optional()
                )
            ));
    }
}
