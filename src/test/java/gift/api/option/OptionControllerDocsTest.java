package gift.api.option;

import gift.RestDocsSupport;
import gift.application.option.OptionService;
import gift.domain.category.Category;
import gift.domain.option.Option;
import gift.domain.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("상품 옵션 관리 API 문서화")
class OptionControllerDocsTest extends RestDocsSupport {

    private final OptionService optionService = mock(OptionService.class);

    @Override
    protected Object initController() {
        return new OptionController(optionService);
    }

    @Test
    @DisplayName("상품의 옵션 목록을 조회하면 등록된 옵션이 반환된다")
    void getOptions() throws Exception {
        // given
        Category category = new Category(1L, "음료", "#FF0000", "img", null);
        Product product = new Product(1L, "아이스 아메리카노", 4500, "img", category);
        Option option = new Option(1L, product, "L 사이즈", 100);
        given(optionService.findByProductId(1L)).willReturn(List.of(option));

        // when & then
        mockMvc.perform(get("/api/v1/products/{productId}/options", 1L))
            .andExpect(status().isOk())
            .andDo(document("option/list",
                pathParameters(
                    parameterWithName("productId").description("상품 ID")
                ),
                responseFields(
                    fieldWithPath("result").description("SUCCESS / ERROR"),
                    fieldWithPath("data[].id").description("옵션 ID"),
                    fieldWithPath("data[].name").description("옵션명"),
                    fieldWithPath("data[].quantity").description("재고 수량"),
                    fieldWithPath("error").description("에러 정보 (성공 시 null)").optional()
                )
            ));
    }

    @Test
    @DisplayName("상품에 새 옵션을 등록하면 옵션 목록에 추가된다")
    void createOption() throws Exception {
        // given
        Category category = new Category(1L, "음료", "#FF0000", "img", null);
        Product product = new Product(1L, "아이스 아메리카노", 4500, "img", category);
        Option option = new Option(1L, product, "L 사이즈", 50);
        given(optionService.register(anyLong(), any(), anyInt())).willReturn(option);

        String body = """
            {"name": "L 사이즈", "quantity": 50}
            """;

        // when & then
        mockMvc.perform(post("/api/v1/products/{productId}/options", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andDo(document("option/register",
                pathParameters(
                    parameterWithName("productId").description("상품 ID")
                ),
                requestFields(
                    fieldWithPath("name").description("옵션명 (최대 50자)"),
                    fieldWithPath("quantity").description("재고 수량 (0 이상)")
                ),
                responseFields(
                    fieldWithPath("result").description("SUCCESS / ERROR"),
                    fieldWithPath("data.id").description("옵션 ID"),
                    fieldWithPath("data.name").description("옵션명"),
                    fieldWithPath("data.quantity").description("재고 수량"),
                    fieldWithPath("error").description("에러 정보 (성공 시 null)").optional()
                )
            ));
    }
}
