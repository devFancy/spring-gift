package gift.api.product;

import gift.RestDocsSupport;
import gift.application.product.ProductService;
import gift.domain.category.Category;
import gift.domain.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("상품 카탈로그 API 문서화")
class ProductControllerDocsTest extends RestDocsSupport {

    private final ProductService productService = mock(ProductService.class);

    @Override
    protected Object initController() {
        return new ProductController(productService);
    }

    @Test
    @DisplayName("새 상품을 등록하면 상품 카탈로그에 노출된다")
    void createProduct() throws Exception {
        // given
        Category category = new Category(1L, "음료", "#FF0000", "img", null);
        Product product = new Product(1L, "아이스 아메리카노", 4500, "https://img.com/iced.jpg", category);
        given(productService.create(any(), anyInt(), any(), anyLong())).willReturn(product);

        String body = """
            {"name": "아이스 아메리카노", "price": 4500, "imageUrl": "https://img.com/iced.jpg", "categoryId": 1}
            """;

        // when & then
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andDo(document("product/register",
                requestFields(
                    fieldWithPath("name").description("상품명 (최대 15자)"),
                    fieldWithPath("price").description("상품 가격 (1원 이상)"),
                    fieldWithPath("imageUrl").description("상품 이미지 URL"),
                    fieldWithPath("categoryId").description("카테고리 ID")
                ),
                responseFields(
                    fieldWithPath("result").description("SUCCESS / ERROR"),
                    fieldWithPath("data.id").description("상품 ID"),
                    fieldWithPath("data.name").description("상품명"),
                    fieldWithPath("data.price").description("상품 가격"),
                    fieldWithPath("data.imageUrl").description("상품 이미지 URL"),
                    fieldWithPath("data.categoryId").description("카테고리 ID"),
                    fieldWithPath("error").description("에러 정보 (성공 시 null)").optional()
                )
            ));
    }

    @Test
    @DisplayName("상품을 단건 조회하면 상품 정보를 받는다")
    void getProduct() throws Exception {
        // given
        Category category = new Category(1L, "음료", "#FF0000", "img", null);
        Product product = new Product(1L, "아이스 아메리카노", 4500, "https://img.com/iced.jpg", category);
        given(productService.findById(1L)).willReturn(product);

        // when & then
        mockMvc.perform(get("/api/v1/products/{id}", 1L))
            .andExpect(status().isOk())
            .andDo(document("product/get",
                pathParameters(
                    parameterWithName("id").description("상품 ID")
                ),
                responseFields(
                    fieldWithPath("result").description("SUCCESS / ERROR"),
                    fieldWithPath("data.id").description("상품 ID"),
                    fieldWithPath("data.name").description("상품명"),
                    fieldWithPath("data.price").description("상품 가격"),
                    fieldWithPath("data.imageUrl").description("상품 이미지 URL"),
                    fieldWithPath("data.categoryId").description("카테고리 ID"),
                    fieldWithPath("error").description("에러 정보 (성공 시 null)").optional()
                )
            ));
    }
}
